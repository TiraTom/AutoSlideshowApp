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
import kotlinx.android.synthetic.main.activity_main.*
import java.security.Permissions
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    // アクセス許可管理用変数
    private val PERMISSIONS_REQUEST_CODE = 100

    // スライドショー管理用変数
    private var mTimer: Timer? = null
    private var mHandler = Handler()
    private var nowPage: Int = 0

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

    }


    // アクセス権限確認結果取得用のメソッド
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSlideshowContents()
                } else {
                    // TODO アプリを閉じる　（ここでいいかもよくわからないけど）
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
                if (imageList.size - 1 <= nowPage){
                    nowPage = 0
                } else {
                    nowPage++
                }

                imageView.setImageURI(imageList[nowPage])

                pauseSlideshow()
            }
            R.id.start_pause_button -> {
                // TODO ボタン対応
            }
            R.id.proceed_button -> {
                if (nowPage == 0){
                    nowPage = imageList.size - 1
                } else {
                    nowPage--
                }

                imageView.setImageURI(imageList[nowPage])

                pauseSlideshow()
            }
        }
    }

    // スライドショーを再生させるメソッド
    private fun startSlideshow() {

        mTimer = Timer()


        // スライドショー再生中は進む・戻るボタンを押せないようにする
        changeButtonAvailability(false)

    }


    // スライドショーを停止させるメソッド
    private fun pauseSlideshow() {

        // スライドショー停止中は進む・戻るボタンを押せるようにする
        changeButtonAvailability(true)

    }


    // 進む・戻るボタンの有効化/無効化を行うメソッド
    private fun changeButtonAvailability(isEnable: Boolean) {
        back_button.isEnabled = isEnable
        proceed_button.isEnabled = isEnable
    }


}
