package jp.osdn.gokigen.aira01b.preference;

/**
 *
 *
 *
 */
public interface ICameraPropertyAccessor
{
    String FRAME_GRID = "frame_grid";
    String FRAME_GRID_DEFAULT_VALUE = "0";

    String IMAGE_CONVERTER = "image_converter";
    String IMAGE_CONVERTER_DEFAULT_VALUE = "0";

    String TAKE_MODE =  "take_mode";
    String TAKE_MODE_DEFAULT_VALUE =  "P";

    String COLOR_TONE = "color_tone";
    String COLOR_TONE_DEFAULT_VALUE = "I_FINISH";

    String AE_MODE = "ae_mode";
    String AE_MODE_DEFAULT_VALUE = "AE_ESP";

    String WB_MODE = "wb_mode";
    String WB_MODE_DEFAULT_VALUE = "WB_AUTO";

    String EXPOSURE_COMPENSATION = "exposure_compensation";
    String EXPOSURE_COMPENSATION_DEFAULT_VALUE = "0";

    String SHUTTER_SPEED = "shutter_speed";
    String SHUTTER_SPEED_DEFAULT_VALUE = "60";

    String APERTURE = "aperture";
    String APERTURE_DEFAULT_VALUE = "1";

    String ISO_SENSITIVITY = "iso_sensitivity";
    String ISO_SENSITIVITY_DEFAULT_VALUE = "Auto";

    String LIVE_VIEW_QUALITY = "live_view_quality";
    String LIVE_VIEW_QUALITY_DEFAULT_VALUE = "QVGA";

    String SOUND_VOLUME_LEVEL = "sound_volume_level";
    String SOUND_VOLUME_LEVEL_DEFAULT_VALUE = "OFF";

    String RAW = "raw";
    String SHOOT_ONLY_CAMERA = "only_camera";

    String PHONE_CAMERA_ID = "phone_camera_id";
    String PHONE_CAMERA_ID_DEFAULT_VALUE = "0";

    String PHONE_CAMERA_ROTATION = "camera_rotation";
    String PHONE_CAMERA_ROTATION_DEFAULT_VALUE = "90";

    String EXIT_APPLICATION = "exit_application";

    String PLAYBACK_CAMERA = "playback_camera";
    String PLAYBACK_PHONE = "playback_phone";

    String SHOW_SAMPLE_IMAGE = "show_sample_image";
    String SHOW_SAMPLE_IMAGE_DEFAULT_VALUE = "0";

    String SELECT_SAMPLE_IMAGE = "select_sample_image";

    String USE_CUSTOM_SPLASH = "use_custom_splash";
    String SELECT_SPLASH_IMAGE = "select_splash_image";

    String SCENE_MANIPULATE_IMAGE = "manipulate_image";

    String SHOW_GRID_STATUS = "show_grid";
    String SHOW_FOCUS_ASSIST_STATUS = "show_focus_assist";

    String LEVEL_GAUGE = "level_gauge";

    int CHOICE_SPLASH_SCREEN = 10;

    int SELECT_SAMPLE_IMAGE_CODE = 110;
    int SELECT_SPLASH_IMAGE_CODE = 120;

    String getLiveViewSize();
    void restoreCameraSettings(Callback callback);
    void storeCameraSettings(Callback callback);

    interface Callback
    {
        void stored(boolean result);
        void restored(boolean result);
    }
}
