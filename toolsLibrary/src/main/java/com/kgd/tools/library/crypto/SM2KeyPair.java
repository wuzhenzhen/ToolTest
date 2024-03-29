package com.kgd.tools.library.crypto;

import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;


/**
 * SM2密钥对Bean
 * @author Potato
 *
 */
public class SM2KeyPair {

	private final ECPoint publicKey;
	private final BigInteger privateKey;

	SM2KeyPair(ECPoint publicKey, BigInteger privateKey) {
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	public ECPoint getPublicKey() {
		return publicKey;
	}

	public BigInteger getPrivateKey() {
		return privateKey;
	}

}
