package com.lineage.echo.encryptions;

import com.lineage.server.types.UByte8;
import com.lineage.server.types.UChar8;
import com.lineage.server.types.ULong32;

/**
 * 加密解密模組<br>
 * 類名稱：Encryption<br>
 * 創建人:Warrior<br>
 * 修改時間：2017年8月19日 下午6:39:26<br>
 * 修改人:QQ:759347094<br>
 * 修改備註:<br>
 * @version<br>
 */
public final class Encryption {
	private Blowfish _Blowfish;
	private UByte8 ub8;
	private UChar8 uc8;
	private ULong32 ul32;

	private char[] Dac1;
	private char[] Eac1;

	private long[] encodeKey = { 0, 0 };
	private long[] decodeKey = { 0, 0 };

	public boolean le;

	public Encryption() {
		ub8 = new UByte8();
		uc8 = new UChar8();
		ul32 = new ULong32();
		Dac1 = new char[400];
		Eac1 = new char[5000];
		_Blowfish = new Blowfish();
	}

	public UByte8 getUByte8() {
		return ub8;
	}

	public UChar8 getUChar8() {
		return uc8;
	}

	public ULong32 getULong32() {
		return ul32;
	}

	/**
	 * 設置加密解密模組
	 * @param l
	 */
	public void initKeys(long l) {
		long al[] = { l, 0x930fd7e2L };
		_Blowfish.getSeeds(al);
		encodeKey[0] = decodeKey[0] = al[0];
		encodeKey[1] = decodeKey[1] = al[1];
		//System.out.println("設置加密解密模組:" + encodeKey[0]+" / "+encodeKey[1]);
		//System.out.println("設置加密解密模組:" + decodeKey[0]+" / "+decodeKey[1]);

		le = true;
	}

	/**
	 * 加密
	 * @param ac
	 * @return
	 */
	public char[] encrypt(char ac[]) {
		long l = ul32.fromArray(ac);
		_encrypt(ac);
		encodeKey[0] ^= l;
		encodeKey[1] = ul32.add(encodeKey[1], 0x287effc3L);
		return ac;
	}

	/**
	 * 解密
	 * @param ac
	 * @param size
	 * @return
	 */
	public char[] decrypt(char ac[], int size) {
		_decrypt(ac, size);
		long l = ul32.fromArray(ac);
		decodeKey[0] ^= l;
		decodeKey[1] = ul32.add(decodeKey[1], 0x287effc3L);
		return ac;
	}

	/**
	 * 加密處理
	 * @param ac
	 * @return
	 */
	private char[] _encrypt(char ac[]) {
		Eac1 = uc8.fromArray(encodeKey, Eac1);
		ac[0] ^= Eac1[0];
		for (int j = 1; j < ac.length; j++) {
			ac[j] ^= ac[j - 1] ^ Eac1[j & 7];
		}
		ac[3] = (char) (ac[3] ^ Eac1[2]);
		ac[2] = (char) (ac[2] ^ ac[3] ^ Eac1[3]);
		ac[1] = (char) (ac[1] ^ ac[2] ^ Eac1[4]);
		ac[0] = (char) (ac[0] ^ ac[1] ^ Eac1[5]);
		return ac;
	}

	/**
	 * 解密處理
	 * @param ac
	 * @param size
	 * @return
	 */
	private char[] _decrypt(char ac[], int size) {
		Dac1 = uc8.fromArray(decodeKey, Dac1);
		char c = ac[3];
		ac[3] ^= Dac1[2];
		char c1 = ac[2];
		ac[2] ^= c ^ Dac1[3];
		char c2 = ac[1];
		ac[1] ^= c1 ^ Dac1[4];
		char c3 = (char) (ac[0] ^ c2 ^ Dac1[5]);
		ac[0] = (char) (c3 ^ Dac1[0]);
		for (int j = 1; j < size; j++) {
			char c4 = ac[j];
			ac[j] ^= Dac1[j & 7] ^ c3;
			c3 = c4;
		}
		return ac;
	}

}