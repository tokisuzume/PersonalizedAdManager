package net.cybefoxlab.nutil;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;

/**
 * Created by tokisuzume on 2018/05/23.
 */
public class PersonalizedAdManager {
    /**
     * データ保存のグループ名
     */
    private final String  DATA_GROUP_NAME = "DATA_AD";

    /**
     * パーソナライズ広告かどうかの初期値
     */
    private final boolean VALUE_DEFAULT_AD_PERSONALIZED = true;

    /**
     * パーソナライズ広告かどうかを保持するキー
     */
    private final String KEY_AD_PERSONALIZED = "KEY_AD_PERSONALIZED";

    private Context context;

    /**
     * コンストラクタ
     * @param context コンテキスト
     */
    public PersonalizedAdManager(Context context){
        this.context = context;
    }

    /**
     * パーソナライズ広告同意のチェック処理
     * @return true:パーソナライズ広告 false:非パーソナライズ広告
     */
    public boolean isPersonalized(){
        SharedPreferences data = getSharedPreferences();
        return data.getBoolean(KEY_AD_PERSONALIZED, VALUE_DEFAULT_AD_PERSONALIZED);
    }

    /**
     * パーソナライズ広告対応フラグの更新
     * @param consentStatus GDPR同意情報のステータス(PERSONALIZEDとNON_PERSONALIZED以外は実行時例外とする)
     */
    public void updateAdmobPersonalizedStatus(ConsentStatus consentStatus){
        switch (consentStatus){
            case PERSONALIZED:
                updatePersonalized();
                break;
            case NON_PERSONALIZED:
                updateNonPersonalized();
                break;
            case UNKNOWN:
            default:
                throw new RuntimeException("Arguments [consentStatus] is not PERSONALIZED or NON_PERSONALIZED.");
        }
    }

    /**
     * SharedPreferencesの取得
     * @return SharedPreferences
     */
    private SharedPreferences getSharedPreferences(){
        return this.context.getSharedPreferences(DATA_GROUP_NAME, Context.MODE_PRIVATE);
    }

    /**
     * SharedPreferences読み書き用のEditor取得
     * @return editor
     */
    private SharedPreferences.Editor getSharedPreferencesEditor(){
        return getSharedPreferences().edit();
    }

    /**
     * パーソナライズ広告に更新
     */
    public void updatePersonalized(){
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        editor.putBoolean(KEY_AD_PERSONALIZED, true);
        editor.apply();
    }

    /**
     * 非パーソナライズ広告に更新
     */
    public void updateNonPersonalized(){
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        editor.putBoolean(KEY_AD_PERSONALIZED, false);
        editor.apply();
    }

    /**
     * 公告リクエスト生成
     * @return 広告リクエスト
     */
    public AdRequest makeAdRequest(){
        AdRequest.Builder builder;
        if(!isPersonalized()){
            // 非パーソナライズ広告への対応
            Bundle extras = new Bundle();
            extras.putString("npa", "1");

            builder = builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
        }
        return builder.build();
    }
}
