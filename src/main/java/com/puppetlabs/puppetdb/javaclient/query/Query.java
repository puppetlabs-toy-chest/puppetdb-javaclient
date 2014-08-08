/**
 * Copyright (c) 2013 Puppet Labs, Inc. and other contributors, as listed below.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *   Puppet Labs
 */
package com.puppetlabs.puppetdb.javaclient.query;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.puppetlabs.puppetdb.javaclient.impl.GsonProvider;
import com.puppetlabs.puppetdb.javaclient.model.Fact;
import com.puppetlabs.puppetdb.javaclient.model.Node;
import com.puppetlabs.puppetdb.javaclient.model.Resource;
import com.puppetlabs.puppetdb.javaclient.query.OrderBy.OrderByField;

/**
 * Helper class for building queries consisting of one or several expressions.
 */
public abstract class Query {
	static abstract class AbstractExpression<T> implements Expression<T> {
		@Override
		public void appendTo(Map<String, String> queryParameters) {
			StringBuilder bld = new StringBuilder();
			toJSON(bld);
			queryParameters.put("query", bld.toString());
		}
	}

	static class Binary<T> extends OpExpression<T> {
		private final Identifier<T> lhs;

		private final Expression<?> rhs;

		Binary(Identifier<T> lhs, Expression<?> rhs, String operator) {
			super(operator);
			if(lhs == null || rhs == null)
				throw new IllegalArgumentException("An '" + operator + "' cannot be operate on null expressions");
			this.lhs = lhs;
			this.rhs = rhs;
		}

		@Override
		void appendPredicateValue(StringBuilder bld) {
			lhs.toJSON(bld);
			bld.append(',');
			rhs.toJSON(bld);
		}
	}

	static class Ident<T> extends Literal<T> implements Field<T> {
		Ident(String name) {
			super(name);
		}
	}

	static class Literal<T> extends AbstractExpression<T> {
		private final Object literal;

		Literal(Object literal) {
			this.literal = literal;
		}

		@Override
		public void toJSON(StringBuilder bld) {
			bld.append(GsonProvider.toJSON(literal));
		}
	}

	static class NAry<T> extends OpExpression<T> {
		private final Expression<?>[] expressions;

		NAry(Expression<T> e1, Expression<T> e2, Expression<T> e3, String operator) {
			super(operator);
			this.expressions = new Expression<?>[] { e1, e2, e3 };
		}

		NAry(Expression<T> e1, Expression<T> e2, String operator) {
			super(operator);
			this.expressions = new Expression<?>[] { e1, e2 };
		}

		NAry(List<Expression<T>> expressions, String operator) {
			super(operator);
			int ec;
			if(expressions == null || (ec = expressions.size()) < 2)
				throw new IllegalArgumentException("An '" + operator + "' operator must be applied to least two expressions");
			this.expressions = expressions.toArray(new Expression<?>[ec]);
		}

		@Override
		void appendPredicateValue(StringBuilder bld) {
			int top = expressions.length;
			expressions[0].toJSON(bld);
			for(int idx = 1; idx < top; ++idx) {
				bld.append(',');
				expressions[idx].toJSON(bld);
			}
		}
	}

	static abstract class OpExpression<T> extends AbstractExpression<T> {
		private final String operator;

		OpExpression(String operator) {
			this.operator = operator;
		}

		abstract void appendPredicateValue(StringBuilder query);

		@Override
		public void toJSON(StringBuilder bld) {
			bld.append("[\"");
			bld.append(operator);
			bld.append("\",");
			appendPredicateValue(bld);
			bld.append(']');
		}

		@Override
		public String toString() {
			StringBuilder bld = new StringBuilder();
			toJSON(bld);
			return bld.toString();
		}
	}

	static class QualifiedIdent<T> extends Literal<T> implements Identifier<T> {
		QualifiedIdent(String qualifier, String name) {
			super(new String[] { qualifier, name });
		}
	}

	static class Unary<T> extends OpExpression<T> {
		private final Expression<T> expression;

		Unary(Expression<T> expression, String operator) {
			super(operator);
			if(expression == null)
				throw new IllegalArgumentException("An '" + operator + "' cannot be applied on a null expression");
			this.expression = expression;
		}

		@Override
		void appendPredicateValue(StringBuilder bld) {
			expression.toJSON(bld);
		}
	}

	/**
	 * <b>Matches if: all</b> of its arguments would match..
	 *
	 * @param e1
	 *            The first expression
	 * @param e2
	 *            The second expression
	 * @return The <code>or</code> expression
	 */
	public static <T> Expression<T> and(Expression<T> e1, Expression<T> e2) {
		return new NAry<T>(e1, e2, "and");
	}

	/**
	 * <b>Matches if: all</b> of its arguments would match..
	 *
	 * @param e1
	 *            The first expression
	 * @param e2
	 *            The second expression
	 * @param e3
	 *            The third expression
	 * @return The <code>or</code> expression
	 */
	public static <T> Expression<T> and(Expression<T> e1, Expression<T> e2, Expression<T> e3) {
		return new NAry<T>(e1, e2, e3, "and");
	}

	/**
	 * <b>Matches if: all</b> of its arguments would match..
	 *
	 * @param expressions
	 *            A list of expressions (at least two)
	 * @return The <code>or</code> expression or the first expression of the list when the list contains only one element
	 */
	public static <T> Expression<T> and(List<Expression<T>> expressions) {
		return new NAry<T>(expressions, "and");
	}

	/**
	 * <b>Matches if:</b> the field’s actual value is exactly the same as the provided value. Note that this
	 * does not coerce values — the provided value must be the same data type as the field. In particular, be aware that:
	 * <ul>
	 * <li>Most fields are strings.</li>
	 * <li>Some fields are booleans.</li>
	 * </ul>
	 *
	 * @param identifier
	 *            The identifier denoting a field, named fact, or named parameter
	 * @param value
	 *            The value to compare with the identified value
	 * @return The <code>=</code> expression
	 */
	public static <T> Expression<T> eq(Identifier<T> identifier, Object value) {
		return new Binary<T>(identifier, new Literal<Object>(value), "=");
	}

	/**
	 * Creates an identifier that identifies a fact
	 *
	 * @param factName
	 *            The name of the fact
	 * @return The created identifier
	 */
	public static <T> Identifier<Node> fact(String factName) {
		return new QualifiedIdent<Node>("fact", factName);
	}

	/**
	 * Creates an identifier that identifies a field
	 *
	 * @param fieldName
	 *            The name of the field
	 * @return The created identifier
	 */
	public static <T> Field<T> field(String fieldName) {
		return new Ident<T>(fieldName);
	}

	/**
	 * <b>Matches if:</b> the field is greater than the provided value. Coerces the field to float or integer; if it
	 * can’t be coerced, the operator will not match.
	 *
	 * @param identifier
	 *            The identifier denoting a field, named fact, or named parameter
	 * @param value
	 *            The value to compare with the identified value
	 * @return The <code>&gt;</code> expression
	 */
	public static <T> Expression<T> gt(Identifier<T> identifier, Number value) {
		return new Binary<T>(identifier, new Literal<Object>(value), ">");
	}

	/**
	 * <b>Matches if:</b> the field is greater than or equal to the provided value. Coerces the field to float or integer; if it
	 * can’t be coerced, the operator will not match.
	 *
	 * @param identifier
	 *            The identifier denoting a field, named fact, or named parameter
	 * @param value
	 *            The value to compare with the identified value
	 * @return The <code>&gt;=</code> expression
	 */
	public static <T> Expression<T> gtEq(Identifier<T> identifier, Number value) {
		return new Binary<T>(identifier, new Literal<Object>(value), ">=");
	}

	private static <T, S> Expression<T> in(Field<T> field, Field<S> subQueryField, Expression<S> subQuery, String subQueryId) {
		return new Binary<T>(field, new Binary<S>(subQueryField, new Unary<S>(subQuery, subQueryId), "extract"), "in");
	}

	/**
	 * Return a subquery expression that will query for all instances for which <code>field</code> can be found
	 * by selecting <code>subQueryField</code> from the set of facts returned by <code>subQuery</code>.
	 *
	 * @param field
	 *            The field to use as the left hand side of the IN clause
	 * @param subQueryField
	 *            The field to select in the subquery
	 * @param subQuery
	 *            The subquery expression
	 * @return The <code>in</code> expression
	 */
	public static <T> Expression<T> inFacts(Field<T> field, Field<Fact> subQueryField, Expression<Fact> subQuery) {
		return in(field, subQueryField, subQuery, "select-facts");
	}

	/**
	 * Return a subquery expression that will query for all instances where the value of <code>field</code> can be found
	 * by in the set obtained by selecting <code>subQueryField</code> using <code>subQuery</code>.
	 *
	 * @param field
	 *            The field to use as the left hand side of the IN clause
	 * @param subQueryField
	 *            The field to select in the subquery
	 * @param subQuery
	 *            The subquery expression
	 * @return The <code>in</code> expression
	 */
	public static <T> Expression<T> inResources(Field<T> field, Field<Resource> subQueryField, Expression<Resource> subQuery) {
		return in(field, subQueryField, subQuery, "select-resources");
	}

	/**
	 * <b>Matches if:</b> the field is less than the provided value. Coerces the field to float or integer; if it
	 * can’t be coerced, the operator will not match.
	 *
	 * @param identifier
	 *            The identifier denoting a field, named fact, or named parameter
	 * @param value
	 *            The value to compare with the identified value
	 * @return The <code>&lt;</code> expression
	 */
	public static <T> Expression<T> lt(Identifier<T> identifier, Number value) {
		return new Binary<T>(identifier, new Literal<Object>(value), "<");
	}

	/**
	 * <b>Matches if:</b> the field is less than or equal to the provided value. Coerces the field to float or integer; if it
	 * can’t be coerced, the operator will not match.
	 *
	 * @param identifier
	 *            The identifier denoting a field, named fact, or named parameter
	 * @param value
	 *            The value to compare with the identified value
	 * @return The <code>&lt;=</code> expression
	 */
	public static <T> Expression<T> ltEq(Identifier<T> identifier, Number value) {
		return new Binary<T>(identifier, new Literal<Object>(value), "<=");
	}

	/**
	 * <p>
	 * <b>Matches if:</b> the field’s actual value matches the provided regular expression.
	 * <p>
	 * The following example would match if the <code>certname</code> field’s actual value resembled something like
	 * <code>www03.example.com</code>
	 * </p>
	 *
	 * <pre>
	 * match(&quot;certname&quot;, Pattern.compile(&quot;www\\d+\\.example\\.com&quot;))
	 * </pre>
	 *
	 * @param identifier
	 *            The identifier denoting a field, named fact, or named parameter
	 * @param pattern
	 *            The regular expression to match with the identified value
	 * @return The match expression
	 */
	public static <T> Expression<T> match(Identifier<T> identifier, String pattern) {
		return new Binary<T>(identifier, new Literal<Object>(pattern), "~");
	}

	/**
	 * <b>Matches if:</b> its argument would not match.
	 *
	 * @param expression
	 *            The expression that will be negated
	 * @return The <code>not</code> expression
	 */
	public static <T> Expression<T> not(Expression<T> expression) {
		return new Unary<T>(expression, "not");
	}

	/**
	 * <b>Matches if: at least one</b> of its arguments would match..
	 *
	 * @param e1
	 *            The first expression
	 * @param e2
	 *            The second expression
	 * @return The <code>or</code> expression
	 */
	public static <T> Expression<T> or(Expression<T> e1, Expression<T> e2) {
		return new NAry<T>(e1, e2, "or");
	}

	/**
	 * <b>Matches if: at least one</b> of its arguments would match..
	 *
	 * @param e1
	 *            The first expression
	 * @param e2
	 *            The second expression
	 * @param e3
	 *            The third expression
	 * @return The <code>or</code> expression
	 */
	public static <T> Expression<T> or(Expression<T> e1, Expression<T> e2, Expression<T> e3) {
		return new NAry<T>(e1, e2, e3, "or");
	}

	/**
	 * <b>Matches if: at least one</b> of its arguments would match..
	 *
	 * @param expressions
	 *            A list of expressions (at least two)
	 * @return The <code>or</code> expression or the first expression of the list when the list contains only one element
	 */
	public static <T> Expression<T> or(List<Expression<T>> expressions) {
		return new NAry<T>(expressions, "or");
	}

	/**
	 * Order the result obtained using the given <code>expression</code> using <code>fields</code>.
	 *
	 * @param expression
	 *            The query expression
	 * @param fields
	 *            The fields to order by
	 * @return The created OrderBy instance
	 */
	public static <T> OrderBy<T> orderBy(Expression<T> expression, List<OrderByField<T>> fields) {
		return new OrderBy<T>(expression, fields);
	}

	/**
	 * Order the result obtained using the given <code>expression</code> using <code>fields</code>.
	 *
	 * @param expression
	 *            The query expression
	 * @param field
	 *            The field to order by
	 * @return The created OrderBy instance
	 */
	public static <T> OrderBy<T> orderBy(Expression<T> expression, OrderByField<T> field) {
		return orderBy(expression, Collections.singletonList(field));
	}

	/**
	 * Order the result obtained using the given <code>expression</code> using <code>fields</code>.
	 *
	 * @param expression
	 *            The query expression
	 * @param field
	 *            The primary field to order by
	 * @param field
	 *            The secondary field to order by
	 * @return The created OrderBy instance
	 */
	public static <T> OrderBy<T> orderBy(Expression<T> expression, OrderByField<T> field1, OrderByField<T> field2) {
		List<OrderByField<T>> list = Arrays.asList(field1, field2);
		return orderBy(expression, list);
	}

	/**
	 * Order the result using the given <code>field</code> in either ascending or descending order given
	 * the boolean <code>descending</code> parameter.
	 *
	 * @param field
	 *            The field to order by
	 * @param descending
	 *            <code>false</code> for ascending order, <code>true</code> for descending order
	 * @return
	 */
	public static <T> OrderByField<T> orderByField(Field<T> field, boolean descending) {
		return new OrderByField<T>(field, descending);
	}

	/**
	 * Paginate the ordered result obtained using the given <code>orderBy</code> in accordance with <code>offset</code> and
	 * <code>limit</code>. If the <code>includeTotal</code> parameter is set, then the total number of entries can be retrieved
	 * from the in the result when the query is executed.
	 *
	 * @param orderBy
	 * @param offset
	 * @param limit
	 * @param includeTotal
	 * @return
	 */
	public static <T> Parameters<T> paging(OrderBy<T> orderBy, int offset, int limit, boolean includeTotal) {
		return new Paging<T>(orderBy, offset, limit, includeTotal);
	}

	/**
	 * Creates an identifier that identifies a resource parameter
	 *
	 * @param parameterName
	 *            The name of the resource parameter
	 * @return The created identifier
	 */
	public static Identifier<Resource> parameter(String parameterName) {
		return new QualifiedIdent<Resource>("parameter", parameterName);
	}
}
