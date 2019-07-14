package tiratom.techacademy.autoslideshowapp

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    // アクセス許可管理用変数
    private val PERMISSIONS_REQUEST_CODE = 100

    // スライドショー管理用変数
    private var mTimer: Timer? = null
    private var mHandler = Handler()
    private var nowIndex: Int = 0

    // スライドショー画像格納用変数
    private var imageList = mutableListOf<Uri>()

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

        if (imageList.size > 0) {
            imageView.setImageURI(imageList[nowIndex])
            startSlideshow()
        } else {
            // ボタンの無効化
            changeButtonAvailability(false)
            start_pause_button.isEnabled = false

            Snackbar.make(mainView, "表示画像がありません", Snackbar.LENGTH_INDEFINITE)
                .setAction("Ok"){}
                .show()

        }
    }


    // アクセス権限確認結果取得用のメソッド
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSlideshowContents()
                } else {
                    // 必要な権限がなければアプリ終了
                    finish()
                }
            }
        }
    }

    // スライドショー用画像の取得メソッド
    private fun getSlideshowContents() {

        var resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.run {
            if (cursor.moveToFirst()) {
                do {
                    // indexからid取得、そのIDから画像のURIを取得し変数に格納
                    val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                    val id = cursor.getLong(fieldIndex)
                    val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                    imageList.add(imageUri)

                } while (cursor.moveToNext())
            }
        }
    }


    override fun onClick(p0: View) {

        when (p0.id) {
            R.id.back_button -> {
                imageView.setImageURI(imageList[getNextSlideIndex(false)])

                pauseSlideshow()
            }
            R.id.start_pause_button -> {

                if (mTimer == null) {
                    startSlideshow()

                    // ボタンのラベル変更
                    start_pause_button.text = "停止"

                } else {
                    pauseSlideshow()

                    // ボタンのラベル変更
                    start_pause_button.text = "再生"

                }
            }
           R.id.proceed_button -> {
                imageView.setImageURI(imageList[getNextSlideIndex(true)])

                pauseSlideshow()
            }
        }
    }

    // スライドショーを再生させるメソッド
    private fun startSlideshow() {

        if (imageList.size == 0){
            return
        }

        if (mTimer == null){
            mTimer = Timer()
            mTimer!!.schedule(object: TimerTask(){
                override fun run() {
                    mHandler.post {
                        imageView.setImageURI(imageList[getNextSlideIndex(true)])
                    }
                }
            }, 2000, 2000)

            // ボタンのラベルを「停止」に変更する
            start_pause_button.text = "停止"

            // スライドショー再生中は進む・戻るボタンを押せないようにする
            changeButtonAvailability(false)
        }
    }


    // スライドショーを停止させるメソッド
    private fun pauseSlideshow() {

        if (mTimer != null){
            mTimer!!.cancel()
            mTimer = null
        }

        // スライドショー停止中は進む・戻るボタンを押せるようにする
        changeButtonAvailability(true)

    }


    // 進む・戻るボタンの有効化/無効化を行うメソッド
    private fun changeButtonAvailability(isEnable: Boolean) {
        back_button.isEnabled = isEnable
        proceed_button.isEnabled = isEnable
    }

    // imageListについて、次に表示するスライド画像のindexを取得するメソッド
    // doesProceedがtrueなら、１つ後の画像
    // doesProceedがfalseなら、１つ前の画像　のindexを取得する
     private fun getNextSlideIndex(doesProceed: Boolean) :Int {

        if (doesProceed) {
            // ----------------------
            // １つ後のスライドを取得する
            // ----------------------
            if (imageList.size - 1 <= nowIndex){
                nowIndex = 0
            } else {
                nowIndex++
            }
        } else {
            // ----------------------
            // １つ前のスライドを取得する
            // ----------------------
            if (nowIndex == 0){
                nowIndex = imageList.size - 1
            } else {
                nowIndex--
            }
        }

        return nowIndex
    }


}
