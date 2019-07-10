package tiratom.techacademy.autoslideshowapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.security.Permissions

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val PERMISSIONS_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ----------------------------------
        // 外部ストレージへのアクセス許可の確認
        // ----------------------------------

        // Android6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態の確認
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                // スライドショー用画像の取得
                getSlideshowContents()
            } else {
                // 許可されていないので確認ダイアログを表示
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
        }
        // Android 5.0系
        else {
            getSlideshowContents()
        }
        // ----------------------------------


        // スライドショー用ボタンの動作の設定
        back_button.setOnClickListener(this)
        start_pause_button.setOnClickListener(this)
        proceed_button.setOnClickListener(this)

    }


    // アクセス権限確認結果取得用のメソッド
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSlideshowContents()
                } else {
                    // アプリを閉じる　（ここでいいかもよくわからないけど）
                }
            }
        }
    }

    // スライドショー用画像の取得メソッド
    private fun getSlideshowContents() {

    }



    override fun onClick(p0: View) {

        when(p0.id) {
            R.id.back_button -> { showOtherPicture() }
            R.id.start_pause_button -> {}
            R.id.proceed_button -> { showOtherPicture(false)}
        }
    }


    // １つ前または１つ後の画像を表示するメソッド
    private fun showOtherPicture(isNext: Boolean = true) {

    }

    // スライドショーを再生させるメソッド
    private fun startSlideshow(){


        // スライドショー再生中は進む・戻るボタンを押せないようにする
        changeButtonAvailability(false)

    }


    // スライドショーを停止させるメソッド
    private fun pauseSlideshow(){

        // スライドショー停止中は進む・戻るボタンを押せるようにする
        changeButtonAvailability(true)

    }


    // 進む・戻るボタンの有効化/無効化を行うメソッド
    private fun changeButtonAvailability(isEnable: Boolean) {

    }


}
