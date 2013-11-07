package com.puppetlabs.puppetdb.javaclient.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * A POJO describing a Puppet Catalog.
 */
public class Catalog extends Entity {
	/**
	 * A POJO that represents catalog data
	 */
	public static class Data {
		private String name;

		private String version;

		@SerializedName("transaction-uuid")
		private String transactionUUID;

		private List<Edge> edges;

		private List<Resource> resources;

		/**
		 * Every relationship between any two resources in the catalog, which may have been made with chaining arrows, metaparameters, or
		 * the require
		 * function
		 * 
		 * @return the edges
		 */
		public List<Edge> getEdges() {
			return edges;
		}

		/**
		 * The name of the node for which the catalog was compiled
		 * 
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Contains every resource in the catalog.
		 * 
		 * @return the resources
		 */
		public List<Resource> getResources() {
			return resources;
		}

		/**
		 * A string used to match the catalog with the corresponding report that was issued during the same puppet run. This field may be
		 * null. (Note: support for this field was introduced in Version 3 of the “replace catalog” command. Versions prior to version 3
		 * will populate this field with a null value.
		 * 
		 * @return The transaction uuid or <code>null</code>
		 */
		public String getTransactionUUID() {
			return transactionUUID;
		}

		/**
		 * An arbitrary string that uniquely identifies this specific catalog across time for a single node. This is controlled by Puppet’s
		 * config_version setting and is usually the seconds elapsed since the epoch.
		 * 
		 * @return the version
		 */
		public String getVersion() {
			return version;
		}

		/**
		 * @param edges
		 *            the edges to set
		 */
		public void setEdges(List<Edge> edges) {
			this.edges = edges;
		}

		/**
		 * @param name
		 *            the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @param resources
		 *            the resources to set
		 */
		public void setResources(List<Resource> resources) {
			this.resources = resources;
		}

		/**
		 * @param transactionUUID
		 *            The id to set
		 */
		public void setTransactionUUID(String transactionUUID) {
			this.transactionUUID = transactionUUID;
		}

		/**
		 * @param version
		 *            the version to set
		 */
		public void setVersion(String version) {
			this.version = version;
		}
	}

	/**
	 * An object which represents a relationship between two resources
	 */
	public static class Edge {
		private ResourceSpec source;

		private ResourceSpec target;

		private Relationship relationship;

		/**
		 * @return the relationship
		 */
		public Relationship getRelationship() {
			return relationship;
		}

		/**
		 * @return the source
		 */
		public ResourceSpec getSource() {
			return source;
		}

		/**
		 * @return the target
		 */
		public ResourceSpec getTarget() {
			return target;
		}

		/**
		 * @param relationship
		 *            the relationship to set
		 */
		public void setRelationship(Relationship relationship) {
			this.relationship = relationship;
		}

		/**
		 * @param source
		 *            the source to set
		 */
		public void setSource(ResourceSpec source) {
			this.source = source;
		}

		/**
		 * @param target
		 *            the target to set
		 */
		public void setTarget(ResourceSpec target) {
			this.target = target;
		}
	}

	/**
	 * Metadata identifying the API version to use. Default is 1.
	 */
	public static class Metadata {
		private Integer api_version = 1;

		/**
		 * @return the api_version
		 */
		public Integer getApi_version() {
			return api_version;
		}

		/**
		 * @param api_version
		 *            the api_version to set
		 */
		public void setApi_version(Integer api_version) {
			this.api_version = api_version;
		}
	}

	/**
	 * <p>
	 * An enum used in the relationship key of an {@link Edge} object.
	 * </p>
	 * <p>
	 * <b>Note:</b> Regardless of the relationship type, the “source” resource is always managed before the “target” resource. This means
	 * that, functionally speaking, required-by is a synonym of before and subscription-of is a synonym of notifies. In this catalog format,
	 * the different relationship types preserve information about the origin of the relationship.
	 * </p>
	 */
	public enum Relationship {

		/** Class or defined type <code>containment</code> */
		contains("contains"),

		/** <code>before</code> metaparam on source, or <code>-&gt;</code> chaining arrow */
		before("before"),

		/** <code>require</code> metaparam on target, or <code>require</code> function */
		required_by("required-by"),

		/** <code>notify</code> metaparam on source, or <code>~&gt;</code> chaining arrow */
		notifies("notifies"),

		/** <code>subscribe</code> metaparam on target */
		subscription_of("subscription-of");

		private final String label;

		private Relationship(String label) {
			this.label = label;
		}

		@Override
		public String toString() {
			return label;
		}
	}

	/**
	 * The representation of a resource reference (single-resource kind).
	 */
	public static class ResourceSpec {
		private String type;

		private String title;

		/**
		 * @return the title
		 */
		public String getTitle() {
			return title;
		}

		/**
		 * @return the type
		 */
		public String getType() {
			return type;
		}

		/**
		 * @param title
		 *            the title to set
		 */
		public void setTitle(String title) {
			this.title = title;
		}

		/**
		 * @param type
		 *            the type to set
		 */
		public void setType(String type) {
			this.type = type;
		}
	}

	private Metadata metadata;

	private Data data;

	/**
	 * @return the data
	 */
	public Data getData() {
		return data;
	}

	/**
	 * @return the metadata
	 */
	public Metadata getMetadata() {
		return metadata;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Data data) {
		this.data = data;
	}

	/**
	 * @param metadata
	 *            the metadata to set
	 */
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}
}
