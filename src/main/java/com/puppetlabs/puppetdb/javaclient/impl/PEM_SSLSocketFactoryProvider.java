package com.puppetlabs.puppetdb.javaclient.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.net.ssl.TrustManagerFactory;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.puppetlabs.puppetdb.javaclient.APIPreferences;

/**
 * Provides an SSLSocketFactory that has been configured according to the settings in the injected {@link APIPreferences} where it
 * will take the following preferences into consideration:
 * <dl>
 * <dt>{@link APIPreferences#getPrivateKeyPEM() getPrivateKeyPEM()}</dt>
 * <dd>Mandatory. Used by the service used to authenticate this client.</dd>
 * <dt>{@link APIPreferences#getCertPEM() getCertPerm()}</dt>
 * <dd>Mandatory. Included in the certificate chain for the corresponding public key.</dd>
 * <dt>{@link APIPreferences#getCaCertPEM() getCaCertPEM()}</dt>
 * <dd>Optional. If it is present, then the created factory will use a trust store to validate the certificate. Otherwise it will allow self signed
 * certificates.</dd>
 * <dt>{@link APIPreferences#isAllowAllHosts()}</dt>
 * <dd>If <code>true</code>, then the created factory will disable hostname verification.</dd>
 * </dl>
 */
public class PEM_SSLSocketFactoryProvider implements Provider<SSLSocketFactory> {
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
			Security.addProvider(new BouncyCastleProvider());
			CertificateFactory factory = CertificateFactory.getInstance("X.509", "BC");
			return new SSLSocketFactory(
				SSLSocketFactory.TLS, getKeyStore(factory, PASSWORD), PASSWORD, getTrustStore(factory), null, getTrustStrategy(),
				getHostnameVerifier());
		}
		catch(RuntimeException e) {
			throw e;
		}
		catch(Exception e) {
			throw new ProvisionException("Unable to create SSLSocketFactory", e);
		}
	}

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
	 * Creates a new <code>JKS</code> trust store and initializes it with the CA certificate found in the file {@link APIPreferences#getCaCertPEM()}
	 * unless that preference is <code>null</code> in which case this method will return <code>null</code>.
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
		File caCertPEM = preferences.getCaCertPEM();
		if(caCertPEM == null)
			return null;

		// Set up a trustStore so that we can verify the server certificate
		X509CertificateHolder caCertHolder = readPEMObject(caCertPEM, "ca-cert pem", X509CertificateHolder.class);
		Certificate caCert = generateCertificate(caCertHolder, factory);
		KeyStore trustStore = KeyStore.getInstance("JKS");
		trustStore.load(null);

		// initialize trust manager factory with the read truststore
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(trustStore);
		trustStore.setCertificateEntry("ca-cert-alias", caCert);
		return trustStore;
	}

	/**
	 * This method will return <code>null</code> when the {@link APIPreferences#getCaCertPEM()} is
	 * set. This means that the default trust strategy will be used and that the server certificate
	 * will be validated. If {@link APIPreferences#getCaCertPEM()} returns <code>null</code>, then
	 * this method will return the {@link TrustSelfSignedStrategy}.
	 * 
	 * @return A trust strategy or <code>null</code>.
	 * @see #getTrustStore(CertificateFactory)
	 */
	protected TrustStrategy getTrustStrategy() {
		return preferences.getCaCertPEM() == null
				? new TrustSelfSignedStrategy()
				: null;
	}

	/**
	 * Creates a new <code>JKS</code> key store and initializes it with the private key found in the file {@link APIPreferences#getPrivateKeyPEM()}
	 * and a certificate chain with one entry, the certificate
	 * found in the file {@link APIPreferences#getCertPEM()}.
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
		X509CertificateHolder certHolder = readPEMObject(preferences.getCertPEM(), "cert pem", X509CertificateHolder.class);
		PEMKeyPair keyPair = readPEMObject(preferences.getPrivateKeyPEM(), "private key pem", PEMKeyPair.class);
		KeyStore keyStore = KeyStore.getInstance("JKS");
		keyStore.load(null);
		keyStore.setKeyEntry(
			"key-alias", getPrivateKey(keyPair), password.toCharArray(), new Certificate[] { generateCertificate(certHolder, factory) });
		return keyStore;
	}

	/**
	 * Extracts the private key from a PEM keypair
	 * 
	 * @param keyPair
	 *            The keypair to extract from
	 * @return The private key of the keypair
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	protected PrivateKey getPrivateKey(PEMKeyPair keyPair) throws IOException, GeneralSecurityException {
		byte[] encodedPrivateKey = keyPair.getPrivateKeyInfo().getEncoded();
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
		return KeyFactory.getInstance("RSA").generatePrivate(privateKeySpec);
	}

	/**
	 * Uses the given <code>factory</code> to generate a certificate from the given <code>holder</code>.
	 * 
	 * @param certHolder
	 *            The holder of the certificate
	 * @param factory
	 *            The factory that performs the generation
	 * @return The generated certificate
	 * @throws CertificateException
	 * @throws IOException
	 */
	protected Certificate generateCertificate(X509CertificateHolder certHolder, CertificateFactory factory) throws CertificateException,
			IOException {
		return factory.generateCertificate(new ByteArrayInputStream(certHolder.getEncoded()));
	}

	/**
	 * Reads exactly one object from a PEM file. The object must be of the expected <code>type</code>.
	 * 
	 * @param pemFile
	 *            The PEM file to be read.
	 * @param prefName
	 *            Name of the preference (for exception message only).
	 * @param type
	 *            The expected type of the object.
	 * @return An object of the given type.
	 * @throws IOException
	 *             If the file could not be read.
	 * @throws ProvisionException
	 *             Indicating that the prefernce <code>prefName</code> is missing if the <code>pemFile</code> is null.
	 */
	protected static <T> T readPEMObject(File pemFile, String prefName, Class<T> type) throws IOException, ProvisionException {
		if(pemFile == null)
			throw new ProvisionException("Missing preference setting for '" + prefName + '\'');
		PEMParser reader = new PEMParser(new InputStreamReader(new FileInputStream(pemFile)));
		try {
			Object obj = reader.readObject();
			return type.cast(obj);
		}
		finally {
			reader.close();
		}
	}
}
