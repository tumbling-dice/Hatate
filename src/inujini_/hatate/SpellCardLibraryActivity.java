/**
 * HatateHoutyouAlarm
 *
 * Copyright (c) 2014 @inujini_ (https://twitter.com/inujini_)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */

package inujini_.hatate;

import inujini_.hatate.adapter.SpellCardLibraryAdapter;
import inujini_.hatate.data.Character;
import inujini_.hatate.data.Series;
import inujini_.hatate.data.SpellCard;
import inujini_.hatate.function.Function.Action;
import inujini_.hatate.function.Function.Action1;
import inujini_.hatate.function.Function.Func1;
import inujini_.hatate.function.Function.Predicate;
import inujini_.hatate.linq.Linq;
import inujini_.hatate.reactive.ReactiveAsyncTask;
import inujini_.hatate.sqlite.dao.CharacterDao;
import inujini_.hatate.sqlite.dao.SeriesDao;
import inujini_.hatate.sqlite.dao.SpellCardDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.val;
import lombok.experimental.ExtensionMethod;
import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 *
 */
@ExtensionMethod({Linq.class})
public class SpellCardLibraryActivity extends ExpandableListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expandable_list);
		setSpellCardByCharacter();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu_spellcard_library, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_series:
			setSpellCardBySeries();
			return true;
		case R.id.menu_item_character:
			setSpellCardByCharacter();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	private void setSpellCardBySeries() {
		setTitle(String.format("%s - シリーズ順", getResources().getString(R.string.title_spellcard_library)));
		val prog = new ProgressDialog(this);
		prog.setMessage("スペルカード一覧を取得しています");
		prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		ReactiveAsyncTask.create(new Func1<Context, Map<Series, List<SpellCard>>>() {
			@Override
			public Map<Series, List<SpellCard>> call(Context x) {
				val serieses = SeriesDao.getAllSeries(x);
				val spellCards = SpellCardDao.getHaveSpellCards(x);

				val hasSeriese = spellCards.linq().selectMany(new Func1<SpellCard, Iterable<Long>>() {
					@Override
					public Iterable<Long> call(SpellCard y) {
						ArrayList<Long> list = new ArrayList<Long>();
						for (int id : y.getSeriesId()) {
							list.add((long) id);
						}
						return list;
					}
				}).distinct().toList();

				return serieses.linq().where(new Predicate<Series>() {
					@Override
					public Boolean call(Series y) {
						return hasSeriese.contains(y.getId());
					}
				}).toMap(new Func1<Series, Series>() {
					private int pos = 0;
					@Override
					public Series call(Series y) {
						y.setPosition(pos++);
						return y;
					}
				}, new Func1<Series, List<SpellCard>>() {
					@Override
					public List<SpellCard> call(Series y) {
						val sId = y.getId();
						return spellCards.linq().where(new Predicate<SpellCard>() {
							@Override
							public Boolean call(SpellCard z) {
								int[] ids = z.getSeriesId();
								for(val id : ids) {
									if(sId == id) return true;
								}
								return false;
							}
						}).toList();
					}
				});
			}
		}).setOnPreExecute(new Action() {
			@Override
			public void call() {
				prog.show();
			}
		}).setOnPostExecute(new Action1<Map<Series, List<SpellCard>>>() {
			@Override
			public void call(Map<Series, List<SpellCard>> x) {
				if(prog != null && prog.isShowing())
					prog.dismiss();
				setListAdapter(new SpellCardLibraryAdapter<Series>(x, getApplicationContext()));
				setIndicator();
			}
		}).setOnError(new Action1<Exception>() {
			@Override
			public void call(Exception e) {
				if(prog != null && prog.isShowing())
					prog.dismiss();
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "取得に失敗しました。", Toast.LENGTH_SHORT).show();
			}
		}).execute(getApplicationContext());
	}

	private void setSpellCardByCharacter() {
		setTitle(String.format("%s - キャラクター順", getResources().getString(R.string.title_spellcard_library)));
		val prog = new ProgressDialog(this);
		prog.setMessage("スペルカード一覧を取得しています");
		prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


		ReactiveAsyncTask.create(new Func1<Context, Map<Character, List<SpellCard>>>() {
			@Override
			public Map<Character, List<SpellCard>> call(Context x) {
				val characters = CharacterDao.getAllCharacter(x);
				val spellCards = SpellCardDao.getHaveSpellCards(x);

				val hasCharacters = spellCards.linq().select(new Func1<SpellCard, Long>() {
					@Override
					public Long call(SpellCard y) {
						return (long) y.getCharacterId();
					}
				}).distinct().toList();

				return characters.linq().where(new Predicate<Character>() {
					@Override
					public Boolean call(Character y) {
						return hasCharacters.contains(y.getId());
					}
				}).toMap(new Func1<Character, Character>() {
					private int pos = 0;
					@Override
					public Character call(Character y) {
						y.setPosition(pos++);
						return y;
					}
				}, new Func1<Character, List<SpellCard>>() {
					@Override
					public List<SpellCard> call(Character y) {
						val cId = y.getId();
						return spellCards.linq().where(new Predicate<SpellCard>() {
							@Override
							public Boolean call(SpellCard z) {
								return cId == z.getCharacterId();
							}
						}).toList();
					}
				});
			}
		}).setOnPreExecute(new Action() {
			@Override
			public void call() {
				prog.show();
			}
		}).setOnPostExecute(new Action1<Map<Character, List<SpellCard>>>() {
			@Override
			public void call(Map<Character, List<SpellCard>> x) {
				if(prog != null && prog.isShowing())
					prog.dismiss();
				setListAdapter(new SpellCardLibraryAdapter<Character>(x, getApplicationContext()));
				setIndicator();
			}
		}).setOnError(new Action1<Exception>() {
			@Override
			public void call(Exception e) {
				if(prog != null && prog.isShowing())
					prog.dismiss();
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "取得に失敗しました。", Toast.LENGTH_SHORT).show();
			}
		}).execute(getApplicationContext());
	}

	private void setIndicator() {
		val newDisplay = getWindowManager().getDefaultDisplay();
		val width = newDisplay.getWidth();
		getExpandableListView().setIndicatorBounds(width-50, width);
	}

}
