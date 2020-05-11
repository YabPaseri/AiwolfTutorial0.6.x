# Mini Tips

チュートリアルにするには微妙だったり，重要ではない内容だったり...



## Javaのバージョン管理

pythonにはpyenvっていう管理ツールがある．Javaにもある．

- [GitHub - jEnv](https://github.com/jenv/jenv)
- [Qiita- jEnvのセットアップ&操作方法(Mac)](https://qiita.com/uhooi/items/9a6747084bcfd4df07a6)

プラットフォーム0.6.0からJava11に移行したが，それ以前はJava8で動いていた．過去のエージェント・プラットフォームを動かす可能性も考慮して，バージョンの切り替えができるようにしておくのも，悪くない



## Pythonの環境

公式は，Python3.6.5で動かしている．ちなみに導入されているモジュールはリンク先にまとめられている．

- [人狼知能プロジェクト - pythonモジュール一覧](http://aiwolf.org/python_modules)

モジュールにcondaがいるので，anacondaでpython環境を構築していることがわかる．
anacondaには，環境をyamlファイル用いてインポート・エクスポートする機能がある．
document/sourceフォルダに`aiwolf.yaml`が入っているので，これを使って環境を構築すると，aiwolfという仮想環境がanaconda上に出来上がる．公式と全く同じではないが，ほぼ同じ構成に近づけてある．

yamlから環境を作る方法は，「anaconda yaml」と検索すればヒットすると思う．

- [Qiita - yamlに書いた条件でAnaconda仮想環境をつくる](https://qiita.com/studio_haneya/items/0b05452dca2ae57c7093)

ちなみに，私はpyenv上にanacondaを入れる派である．

- [GitHub - pyenv](https://github.com/pyenv/pyenv)
- [Qiita - pyenvでのPython仮想環境の作り方まとめ](https://qiita.com/ysdyt/items/5008e607343b940b3480)



## Eclipseに自分の環境を入れる

Eclipseの内部は独自の環境になっているので，AutoStarter.iniにpythonエージェントを書き込んで参加させても，python2系が動いて話にならない．そもそもモジュールの追加などができない．それならば，普段使っている環境をEclipseに読み込ませればいい．

1. 実行の構成を開く(既存のものを書き換えるならそれを開く，新規に作るなら普段の作り方で1度作る)
2. 環境タブを開く
3. ターミナルappを開き，`$ printenv`を実行．出力結果を全部コピー
4. 環境タブの中の`貼り付け`を押す
5. いい感じにセットしてくれる



## 5人人狼と15人人狼を別々に実装したい

AbstractRoleAssignPlayerから村人クラスを呼び出して，村人クラスから5人or15人で呼び出すクラスを分けて...としてもいいが，それならAbstractRoleAssignPlayerを使うのをやめてしまおう．

document/sourceフォルダ内に`ExtendedAbstractRoleAssignPlayer.java`が入っている．自分のプロジェクトで任意のパッケージを作成後，適当なところにこのファイルを入れる．`package ~~`の行がエラーを吐くので，自分のパッケージ名に書き換えてから使用する．

`setVillager`で村人をセットしていたところが，`setVillager5`と`setVillager15`でセットするように変わっている．シンプルに5人と15人が切り分けられる．



## Talkの作成が面倒くさい

[GitHub - SampleBasePlayer.java](https://github.com/aiwolf/AIWolfClient/blob/0.6.x/src/org/aiwolf/sample/player/SampleBasePlayer.java)に倣って，ラッパーメソッドを作っておこう
(下の方にstatic○○というメソッドがいくつも作られている)