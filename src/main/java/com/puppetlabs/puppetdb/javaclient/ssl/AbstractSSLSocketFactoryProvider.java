package com.puppetlabs.puppetdb.javaclient.ssl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.KeySpec;

import javax.net.ssl.TrustManagerFactory;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.puppetlabs.puppetdb.javaclient.APIPreferences;

/**
 * Abstract provider for a {@link SSLSocketFactory}.
 */
public abstract class AbstractSSLSocketFactoryProvider implements Provider<SSLSocketFactory> {
	private static final String PASSWORD = "puppet";

	@Inject
	private APIPreferences preferences;

	/**
	 * Creates a new SSL socket factory
	 * 
	 * @return The created factory
	 */
	@Override
	public SSLSocketFactory get() {

		try {
			// We need a factory that can generate a X.509 certificate using BouncyCastleProvideer
			CertificateFactory factory = CertificateFactory.getInstance("X.509");
			KeyStore trustStore = getTrustStore(factory);
			TrustStrategy trustStrategy = trustStore == null
					? new TrustSelfSignedStrategy()
					: null;
			return new SSLSocketFactory(
				SSLSocketFactory.TLS, getKeyStore(factory, PASSWORD), PASSWORD, trustStore, null, trustStrategy, getHostnameVerifier());
		}
		catch(RuntimeException e) {
			throw e;
		}
		catch(Exception e) {
			throw new ProvisionException("Unable to create SSLSocketFactory", e);
		}
	}

	/**
	 * Returns the Certificate for the Certificate Authority or <code>null</code> if no such certificate exists. Returning <code>null</code> will
	 * cause the created SSL factory to accept self signed certificates.
	 * 
	 * @param factory
	 *            The certificate factory to use when generating the certificate
	 * @return The certificate or <code>null</code>.
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	protected abstract Certificate getCACertificate(CertificateFactory factory) throws IOException, GeneralSecurityException;

	/**
	 * Returns the mandatory Host Certificate.
	 * 
	 * @param factory
	 *            The certificate factory to use when generating the certificate
	 * @return The certificate. This method must never return <code>null</code>.
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	protected abstract Certificate getHostCertificate(CertificateFactory factory) throws IOException, GeneralSecurityException;

	/**
	 * Returns a hostname verifier that either allows all hostnames or a
	 * browser compatible hostname verifier depending on the setting of the {@link APIPreferences#isAllowAllHosts()}.
	 * 
	 * @return A hostname verifier
	 * @see AllowAllHostnameVerifier
	 * @see BrowserCompatHostnameVerifier
	 */
	protected X509HostnameVerifier getHostnameVerifier() {
		return preferences.isAllowAllHosts()
				? SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER
				: SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
	}

	/**
	 * Creates a new <code>JKS</code> key store and initializes it with the host private key and host certificate.
	 * 
	 * @param factory
	 *            The factory used when generating the certificate
	 * @param password
	 *            A password used to protect the key
	 * @return The new key store.
	 * @throws ProvisionException
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	protected KeyStore getKeyStore(CertificateFactory factory, String password) throws ProvisionException, IOException,
			GeneralSecurityException {
		KeyStore keyStore = KeyStore.getInstance("JKS");
		keyStore.load(null);
		keyStore.setKeyEntry(
			"key-alias", getPrivateKey(getPrivateKeySpec()), password.toCharArray(), new Certificate[] { getHostCertificate(factory) });
		return keyStore;
	}

	/**
	 * Returns the injected preferences.
	 * 
	 * @return The preferences.
	 */
	protected APIPreferences getPreferences() {
		return preferences;
	}

	/**
	 * Extracts the private key from an RSA key specification.
	 * 
	 * @param privateKey
	 *            The DER encoded private key to extract from
	 * @return The private key of the keypair
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	protected PrivateKey getPrivateKey(KeySpec privateKey) throws IOException, GeneralSecurityException {
		return KeyFactory.getInstance("RSA").generatePrivate(privateKey);
	}

	/**
	 * Returns a RSA specification for the mandatory private key.
	 * 
	 * @return The private key spec. This method must never return <code>null</code>.
	 * @throws KeyException
	 * @throws IOException
	 */
	protected abstract KeySpec getPrivateKeySpec() throws KeyException, IOException;

	/**
	 * Creates a new <code>JKS</code> trust store and initializes it with the CA certificate returned by {@link #getCACertificate(CertificateFactory)}
	 * .
	 * If that method returns <code>null</code> then this method will also return <code>null</code>.
	 * 
	 * @param factory
	 *            The factory used when generating the certificate
	 * @return The new trust store.
	 * @throws ProvisionException
	 * @throws IOException
	 * @throws GeneralSecurityException
	 * @see {@link #getTrustStrategy()}
	 */
	protected KeyStore getTrustStore(CertificateFactory factory) throws ProvisionException, IOException, GeneralSecurityException {
		// Set up a trustStore so that we can verify the server certificate
		Certificate caCert = getCACertificate(factory);
		if(caCert == null)
			return null;

		KeyStore trustStore = KeyStore.getInstance("JKS");
		trustStore.load(null);

		// initialize trust manager factory with the read truststore
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(trustStore);
		trustStore.setCertificateEntry("ca-cert-alias", caCert);
		return trustStore;
	}
}
