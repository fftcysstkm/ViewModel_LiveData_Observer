package com.example.android.unscramble.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    /**
     *  フラグメントに表示する点数、文字、出題済みの単語リスト
     *  バッキングプロパティ：変数を内部向けと外部向けの2種類用意し、隠蔽する方法
     *   ①内部向け：private varとし、viewmodel内でのみ変更可能とする。
     *   ②外部向け：外からはpublic valで読み取り専用とする。getterを①を返すようオーバーライド
     */
    private var _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

    private var _currentWordCount = MutableLiveData(0)
    val currentWordCount: LiveData<Int>
        get() = _currentWordCount

    private val _currentScrambledWord = MutableLiveData<String>()
    val currentScrambleWord: LiveData<String>
        get() = _currentScrambledWord

    // ゲーム中使用する単語のリストと現在表示中の単語
    private var wordList: MutableList<String> = mutableListOf()
    private lateinit var currentWord: String //後で初期化する

    /**
     *  ViewModel作成時、出題文字列取得
     */
    init {
        getNextWord()
    }

    /**
     *  次のスクランブルされた単語を取得する
     */
    private fun getNextWord() {
        currentWord = allWordsList.random()
        val tempWord = currentWord.toCharArray()// shuffleするので配列化

        // スクランブルした単語が元の単語と一致しないようシャッフル
        while (String(tempWord).equals(currentWord, false)) {
            tempWord.shuffle()
        }

        // 出題済みであれば再度単語をランダムに選択
        if (wordList.contains(currentWord)) {
            getNextWord()
        } else {
            // 未出題であれば出題する単語に採用、出題済みリストに追加、回答数更新
            _currentScrambledWord.value = String(tempWord)
            _currentWordCount.value = (_currentWordCount.value)?.inc()
            wordList.add(currentWord)
        }
    }

    /**
     *  最大出題数まで達したかチェック.まだなら次のスクランブル文字列取得
     *  @return 最大出題数まで達したか
     */
    fun nextWord(): Boolean {
        return if (_currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else {
            false
        }
    }

    /**
     *  得点を加算
     */
    private fun increaseScore() {
        _score.value = (_score.value)?.plus(SCORE_INCREASE)
    }

    /**
     * ユーザーの入力が正しいかチェック。正しければ得点加算
     */
    fun isUserWordCorrect(playerWord: String): Boolean {
        if (currentWord.equals(playerWord, true)) {
            increaseScore()
            return true
        }
        return false
    }

    /**
     * 得点と出題済みリストデータをリセット、最初の単語を取得しなおす
     */
    fun reinitializeData() {
        _score.value = 0
        _currentWordCount.value = 0
        wordList.clear()
        getNextWord()
    }

}