package com.yutakomura.infrastructure.security

/**
 * Redisを使用したトークン情報の永続化を行うためのリポジトリインタフェースです。
 */
interface TokenRepository {

    /**
     * 指定されたキーに対応するトークンを取得します。
     *
     * @param key トークンのキー
     * @return トークンオブジェクト。キーに対応するトークンが存在しない場合は null
     */
    fun selectBy(key: Key): Token?

    /**
     * トークンをリポジトリに追加します。
     *
     * @param token 追加するトークンオブジェクト
     * @return 追加が成功した場合は1、そうでない場合は0
     */
    fun insert(token: Token): Int

    /**
     * 複数のトークンをリポジトリに追加します。
     *
     * @param tokens 追加するトークンオブジェクトのリスト
     * @return 追加が成功した場合は追加したトークンオブジェクトの数、そうでない場合は0
     */
    fun insert(tokens: List<Token>): Int

    /**
     * 指定されたキーに対応するトークンをリポジトリから削除します。
     *
     * @param key 削除するトークンのキー
     * @return 削除が成功した場合は1、そうでない場合は0
     */
    fun deleteBy(key: Key): Int

    /**
     * 指定されたキーに対応する複数のトークンをリポジトリから削除します。
     *
     * @param keys 削除するトークンのキーのリスト
     * @return 削除が成功した場合は削除したトークンの数、そうでない場合は0
     */
    fun deleteBy(keys: List<Key>): Int
}
