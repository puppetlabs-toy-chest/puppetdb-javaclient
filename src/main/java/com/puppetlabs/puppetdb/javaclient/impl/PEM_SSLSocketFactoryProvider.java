package com.puppetlabs.puppetdb.javaclient.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.KeySpec;

import com.puppetlabs.puppetdb.javaclient.APIPreferences;
import com.puppetlabs.puppetdb.javaclient.ssl.AbstractSSLSocketFactoryProvider;
import com.puppetlabs.puppetdb.javaclient.ssl.KeySpecFactory;

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
public class PEM_SSLSocketFactoryProvider extends AbstractSSLSocketFactoryProvider {
	private Certificate generateCertificate(File certFile, CertificateFactory factory) throws CertificateException, IOException {
		BufferedInputStream input = new BufferedInputStream(new FileInputStream(certFile));
		try {
			return factory.generateCertificate(input);
		}
		finally {
			input.close();
		}
	}

	@Override
	protected Certificate getCACertificate(CertificateFactory factory) throws IOException, GeneralSecurityException {
		File caCertPEM = getPreferences().getCaCertPEM();
		return caCertPEM == null
				? null
				: generateCertificate(caCertPEM, factory);
	}

	@Override
	protected Certificate getHostCertificate(CertificateFactory factory) throws IOException, GeneralSecurityException {
		File hostCertPEM = getPreferences().getCertPEM();
		if(hostCertPEM == null)
			throw new IOException("Missing required preferences setting for host certificate PEM file");
		return generateCertificate(hostCertPEM, factory);
	}

	@Override
	protected KeySpec getPrivateKeySpec() throws KeyException, IOException {
		return KeySpecFactory.readKeySpec(getPreferences().getPrivateKeyPEM());
	}
}
