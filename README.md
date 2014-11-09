#概要
[包丁で刺されるはたてちゃんアラーム](https://play.google.com/store/apps/details?id=inujini_.hatate)の全ソースコードです。
更新したのにコミットを忘れていることがあるので常に最新とは限りません。

また、Twitter連携部分のConsumer KeyやConsumer Secretは削除しています。

#更新履歴

+ ver 1.2.3
    * 「とどめをささない」→「慈悲はない」に変更
    * スペルカードガチャの結果をわかりやすくしました
    * スペルカード図鑑で所持していないキャラクタ、シリーズは表示しないようにしました
+ ver 1.2.2
    * 取得したスペルカードの通知
+ ver 1.2.1
    * オンラインマニュアルの追加
+ ver 1.2.0
    * スペルカードガチャ実装
    * スペルカード図鑑追加
    * スペルカード履歴にキャラのアイコンがついて見やすくなりました
    * ライセンスの追記（東方16×16シリーズ）
+ ver 1.2.0β2
    * 設定画面に色々とアイコンを追加
    * Twitter連携アカウントを複数設定可能（やめろ）
    * ライセンスの追記（Material Design Icons）
+ ver 1.2.0β
    * スペルカード収集機能の追加
    * 細かなバグフィックス
+ ver 1.1.1
    * 通知に関する設定を別画面へ移動
    * バイブレーションパターンの追加
    * その他細かいバグ修正
+ ver 1.1.0
    * 好感度システムの実装<br>
      →好感度によって悲鳴が変わるようになりました
    * 時刻設定の表示がちゃんと変わらないバグの修正
    * 全く同じ時刻に設定すると即刺しに行くバグの修正
+ ver 1.0.4
    * 確殺機能（スヌーズ機能）の追加
    * 他アプリでの音量変更を反映できていないバグを修正
+ ver 1.0.3b
    * 声量を設定するシークバーがダサすぎたので修正
+ ver 1.0.3
    * スリープ時にLEDを光らせる機能を追加
    * Twitter連携機能の追加
+ ver 1.0.2
    * 声量を画面上で設定できるよう修正
+ ver 1.0.1
    * 端末再起動時に自動でアラームを設定し直すよう修正
+ ver 1.0.0
    * 公開

#バグ報告・要望など

IssueやPull Requestを使ってもいいんですが、[Twitter](https://twitter.com/inujini_)で教えてくれると初動が早いです。

既にネタ切れ感はあるので要望は割とすんなり通るかもしれません。

#ライセンス
ソースコードに関してはMITライセンスを採用しています。以下はその全文となります。

***
The MIT License (MIT)

Copyright (c) 2014 [@inujini_](https://twitter.com/inujini_)

Permission is hereby granted, free of charge, to any person obtaining a copy of
 this software and associated documentation files (the "Software"), to deal in
 the Software without restriction, including without limitation the rights to
 use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 the Software, and to permit persons to whom the Software is furnished to do so,
 subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
***

**ただし、当成果物に含まれている画像リソースに関してはこの限りではありません。**<br>
**はたてちゃんの画像に関する全ての権利は[天篭り](https://twitter.com/tcm_b_c)氏が所有しており、使用・加工・再配布に関しては氏の許諾を得る必要があります。**

## Libary License
当成果物内で使用している各種ライブラリのライセンスに関しては以下の通りとなっています。

+ [Apache 2.0ライセンス](http://www.apache.org/licenses/LICENSE-2.0)で配布されているもの
    * [Twittrer4J](http://twitter4j.org/ja/index.html)
+ MITライセンスで配布されているもの
    * [Lombok](http://projectlombok.org/)

## Icon License
設定画面でのアイコンは[Material Design Icons ((C) Google 2014)](https://github.com/google/material-design-icons/releases) を使用しています。

Material Design Iconsはクリエイティブ・コモンズ・ライセンス4.0の[「表示-継承」](http://creativecommons.org/licenses/by-sa/4.0/deed.ja)に基いて公開されています。ライセンスについては[こちら](http://creativecommons.org/licenses/by-sa/4.0/legalcode)をお読み下さい。

また、東方キャラクタのドット絵アイコンは[東方16×16シリーズ](http://d.hatena.ne.jp/Erl/20090523/1243059517)((C) Erl 2014)を使用しています。

東方16×16シリーズはクリエイティブ・コモンズ・ライセンス2.1の[「表示-非営利」](http://creativecommons.org/licenses/by-nc/2.1/jp/)に基いて公開されています。ライセンスについては[こちら](http://creativecommons.org/licenses/by-nc/2.1/jp/legalcode)をお読み下さい。
