package com.puppetlabs.puppetdb.javaclient.ssl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.KeyException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * Utility class that creates a KeySpecs from PEM files or DER encodings.
 */
public class KeySpecFactory {
	/**
	 * Key types known to this factory
	 */
	public static enum KeyType {
		/** PKCS#1 RSA private key */
		PKCS1,
		/** PCKS#8 private key */
		PKCS8,
		/** X509 public key */
		X509
	}

	static class PKCS1Parser {
		private int pos;

		private final byte[] bytes;

		private final BigInteger[] bis = new BigInteger[8];

		PKCS1Parser(byte[] code) throws KeyException {
			this.bytes = code;
			pos = 0;
			readTag(0x30);
			readLength(); // Read and skip total key length
			skipBI(); // Skip version
			for(int i = 0; i < 8; ++i)
				bis[i] = readBI();
		}

		private RSAPrivateCrtKeySpec keySpec() throws KeyException {
			return new RSAPrivateCrtKeySpec(//
				bis[0], // modulus
				bis[1], // public exponent
				bis[2], // private exponent
				bis[3], // prime P
				bis[4], // prime Q
				bis[5], // prime exponent P
				bis[6], // prime exponent Q
				bis[7] // crt coefficient
			);
		}

		private BigInteger readBI() throws KeyException {
			int len = readBILength();
			byte[] x = new byte[len];
			System.arraycopy(bytes, pos, x, 0, len);
			pos += len;
			return new BigInteger(x);
		}

		private int readBILength() throws KeyException {
			readTag(2);
			return readLength();
		}

		private int readLength() {
			int len = (bytes[pos++] & 0xff);
			if((len & 0x80) == 0x80) {
				int n = len & 0x7f;
				len = 0;
				for(int i = 0; i < n; ++i, ++pos)
					len = (len << 8) | (bytes[pos] & 0xff);
			}
			return len;
		}

		private void skipBI() throws KeyException {
			int len = readBILength();
			pos += len;
		}

		private void readTag(int tag) throws KeyException {
			if(bytes[pos] != tag)
				throw new KeyException("Invalid tag: Expected " + Integer.toHexString(tag) + " but got " +
						Integer.toHexString(bytes[pos] & 0xff) + " at offset " + pos);
			++pos;
		}
	}

	private static final String PKCS8_MARKER = "-----BEGIN PRIVATE KEY-----";

	private static final String PKCS1_MARKER = "-----BEGIN RSA PRIVATE KEY-----";

	private static final String X509_MARKER = "-----BEGIN PUBLIC KEY-----";

	private static final String BEGIN_MARKER = "-----BEGIN ";

	private static KeyType getKeyType(String marker, String inputName) throws KeyException {
		if(PKCS1_MARKER.equals(marker))
			return KeyType.PKCS1;
		if(PKCS8_MARKER.equals(marker))
			return KeyType.PKCS8;
		if(X509_MARKER.equals(marker))
			return KeyType.X509;
		throw new KeyException("Marker not recognized as a key marker: " + marker + " in file: " + inputName);
	}

	private static byte[] readBytes(BufferedReader reader, String endMarker, String inputName) throws IOException {
		String line = null;
		StringBuilder bld = new StringBuilder();

		while((line = reader.readLine()) != null) {
			line = line.trim();
			if(endMarker.equals(line))
				return Base64.decodeBase64(bld.toString());
			bld.append(line);
		}
		throw new IOException("No END marker found in: " + inputName);
	}

	/**
	 * Reads a key spec in PEM format from a reader.
	 * 
	 * @param reader
	 *            The reader from where the PEM contents is read
	 * @param inputName
	 *            Name of input. Typically a file name (used in exceptions only).
	 * @return The created spec.
	 * @throws IOException
	 * @throws KeyException
	 */
	public static KeySpec readKeySpec(BufferedReader reader, String inputName) throws IOException, KeyException {
		String line;
		while((line = reader.readLine()) != null) {
			if(line.startsWith(BEGIN_MARKER)) {
				String marker = line.trim();
				return readKeySpec(getKeyType(marker, inputName), readBytes(reader, marker.replace("BEGIN", "END"), inputName));
			}
		}
		throw new IOException("No BEGIN marker found in: " + inputName);
	}

	/**
	 * Reads a key spec from a PEM file.
	 * 
	 * @param file
	 *            The PEM file to read from
	 * @return The created key spec
	 * @throws IOException
	 * @throws KeyException
	 */
	public static KeySpec readKeySpec(File file) throws IOException, KeyException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "ASCII"));
		try {
			return readKeySpec(reader, file.getAbsolutePath());
		}
		finally {
			reader.close();
		}
	}

	/**
	 * Reads a key spec in from bytes.
	 * 
	 * @param keyType
	 *            The type to use when decoding the bytes.
	 * @param keyData
	 *            The reader from where the PEM contents is read
	 * @return The created spec.
	 * @throws IOException
	 * @throws KeyException
	 */
	public static KeySpec readKeySpec(KeyType keyType, byte[] keyData) throws IOException, KeyException {
		switch(keyType) {
			case PKCS1:
				return new PKCS1Parser(keyData).keySpec();
			case PKCS8:
				return new PKCS8EncodedKeySpec(keyData);
			default:
				return new X509EncodedKeySpec(keyData);
		}
	}
}
