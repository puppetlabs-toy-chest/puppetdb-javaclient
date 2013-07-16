package com.puppetlabs.puppetdb.javaclient.test;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;

import com.google.inject.Provider;
import com.google.inject.ProvisionException;

/**
 * Provides an SSLSocketFactory that has been configured to allow all certificates
 * from any host.
 */
public class InsecureSSLSocketFactoryProvider implements Provider<SSLSocketFactory> {
	@Override
	public SSLSocketFactory get() {
		try {
			return new SSLSocketFactory(new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
					return true;
				}
			}, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		}
		catch(RuntimeException e) {
			throw e;
		}
		catch(Exception e) {
			throw new ProvisionException("Unable to create SSLSocketFactory", e);
		}
	}
}
