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
 * 好感度計算.
 */
public class Love {

	/**
	 * 好感度初期化.
	 * @param count 刺した回数
	 * @return countの2倍
	 */
	public static int init(int count) {
		return count * 2;
	}

	/**
	 * <p>好感度に応じて音声データを選択.</p>
	 * <p>返ってくる音声は以下の通り.</p>
	 * <table>
	 *   <tr>
	 *     <th>好感度</th>
	 *     <th>データ</th>
	 *   </tr>
	 *   <tr>
	 *     <td>75以上</td>
	 *     <td>ugu_love</td>
	 *   </tr>
	 *   <tr>
	 *     <td>-50未満</td>
	 *     <td>ugu_hate</td>
	 *   </tr>
	 *   <tr>
	 *     <td>それ以外</td>
	 *     <td>ugu</td>
	 *   </tr>
	 * </table>
	 * @param love 好感度
	 * @return 好感度に応じた音声データのリソースID
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
	 * <p>好感度計算.</p>
	 * <p>好感度の計算には乱数と刺した回数を用いる.</p>
	 * <p>まず、0～254の乱数から基礎値を求める.乱数と基礎値の対応は以下の通り.</p>
	 * <table>
	 *   <tr>
	 *     <th>乱数範囲</th>
	 *     <th>基礎値</th>
	 *   </tr>
	 *   <tr>
	 *     <td>0</td>
	 *     <td>-100</td>
	 *   </tr>
	 *   <tr>
	 *     <td>1～100</td>
	 *     <td>2</td>
	 *   </tr>
	 *   <tr>
	 *     <td>101～253</td>
	 *     <td>-10</td>
	 *   </tr>
	 *   <tr>
	 *     <td>254</td>
	 *     <td>10</td>
	 *   </tr>
	 *   <tr>
	 *     <td>それ以外</td>
	 *     <td>100</td>
	 *   </tr>
	 * </table>
	 *
	 * <p>もしkillCountが5よりも大きく、なおかつ{@link Random#nextBoolean()}がtrueだった場合、
	 * killCount / 5の値を上記の基礎値に乗算したものを返却する.</p>
	 * <p>5以下の場合は基礎値をそのまま返却する.</p>
	 *
	 * @param killCount 刺した回数
	 * @return 算出結果
	 */
	public static int culc(int killCount) {
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
