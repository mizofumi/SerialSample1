package net.mizofumi;

/**
 * Created by mizof on 2016/11/17.
 */
public interface SerialEventListener {
    void read(String data); //読み出し
    void write_success(); //書き出し成功
    void write_failed(); //書き出し失敗
    void open(); //ポートオープン成功
    void open_failed(String errorMessage); //ポートオープン失敗
    void stop(); //停止時に呼ぶ
    void close(); //ポートクローズ成功
    void close_failed(String errorMessage); //ポートクローズ失敗
}
