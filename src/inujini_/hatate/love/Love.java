/**
 * HatateHoutyouAlarm
 * 
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 * 
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate.love;

import inujini_.hatate.R;

import java.util.Random;

import lombok.val;

/**
 * �D���x�v�Z.
 */
public class Love {

	/**
	 * �D���x������.
	 * 
	 * @param count �h������
	 * @return count��2�{
	 */
	public static int init(int count) {
		return count * 2;
	}

	/**
	 * <p>�D���x�ɉ����ĉ����f�[�^��I��.</p>
	 * <p>�Ԃ��Ă��鉹���͈ȉ��̒ʂ�.</p>
	 * <table>
	 *   <tr>
	 *     <th>�D���x</th>
	 *     <th>�f�[�^</th>
	 *   </tr>
	 *   <tr>
	 *     <td>75�ȏ�</td>
	 *     <td>ugu_love</td>
	 *   </tr>
	 *   <tr>
	 *     <td>-50����</td>
	 *     <td>ugu_hate</td>
	 *   </tr>
	 *   <tr>
	 *     <td>����ȊO</td>
	 *     <td>ugu</td>
	 *   </tr>
	 * </table>
	 * 
	 * @param love �D���x
	 * @return �D���x�ɉ����������f�[�^�̃��\�[�XID
	 */
	public static int getVoice(int love) {
		if(love >= 75) {
			return R.raw.ugu_love;
		} else if (love < -50) {
			return R.raw.ugu_hate;
		} else {
			return R.raw.ugu;
		}
	}

	/**
	 * <p>�D���x�v�Z.</p>
	 * <p>�D���x�̌v�Z�ɂ͗����Ǝh�����񐔂�p����.</p>
	 * <p>�܂��A0�`254�̗��������b�l�����߂�.�����Ɗ�b�l�̑Ή��͈ȉ��̒ʂ�.</p>
	 * <table>
	 *   <tr>
	 *     <th>�����͈�</th>
	 *     <th>��b�l</th>
	 *   </tr>
	 *   <tr>
	 *     <td>0</td>
	 *     <td>-100</td>
	 *   </tr>
	 *   <tr>
	 *     <td>1�`100</td>
	 *     <td>2</td>
	 *   </tr>
	 *   <tr>
	 *     <td>101�`253</td>
	 *     <td>-10</td>
	 *   </tr>
	 *   <tr>
	 *     <td>254</td>
	 *     <td>10</td>
	 *   </tr>
	 *   <tr>
	 *     <td>����ȊO</td>
	 *     <td>100</td>
	 *   </tr>
	 * </table>
	 * 
	 * <p>����killCount��5�����傫���A�Ȃ�����{@link Random#nextBoolean()}��true�������ꍇ�A
	 * killCount / 5�̒l����L�̊�b�l�ɏ�Z�������̂�ԋp����.</p>
	 * <p>5�ȉ��̏ꍇ�͊�b�l�����̂܂ܕԋp����.</p>
	 *
	 * @param killCount �h������
	 * @return �Z�o����
	 */
	// FIXME: Rename(culcLove -> culc)
	public static int culcLove(int killCount) {
		val rnd = new Random();
		val base = rnd.nextInt(255);
		int tmpLove = 0;
		
		if(base == 0) {
			tmpLove = -100;
		} else if(base >= 1 && base <= 100) {
			tmpLove = 2;
		} else if(base >= 101 && base <= 253) {
			tmpLove = -10;
		} else if(base == 254) {
			tmpLove = 10;
		} else {
			// this part is bug.
			tmpLove = 100;
		}

		if(killCount < 5)
			return tmpLove;

		return rnd.nextBoolean() ? tmpLove * (killCount / 5): tmpLove;

	}

}
