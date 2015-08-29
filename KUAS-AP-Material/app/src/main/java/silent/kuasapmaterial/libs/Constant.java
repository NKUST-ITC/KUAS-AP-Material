package silent.kuasapmaterial.libs;

public class Constant {

	public static final String TAG = "HearSilent";

	// Handler status code
	public static final int STATUS_START = 100;
	public static final int STATUS_SUCCESS = 200;
	public static final int STATUS_ERROR = 400;

	// SharedPreferences
	public static final String IS_LOGIN = "is_login";
	public static final String PREF_ID = "id";
	public static final String PREF_PASSWORD = "password";
	public static final String PREF_GENDER = "gender";
	public static final String PREF_STATUS = "pref_status";
	public static final String PREF_MEMBER = "pref_member";
	public static final String PREF_REGISTRATION_ID = "registration_id";
	public static final String PREF_APP_VERSION = "app_version";
	public static final String PREF_MESSAGE_NOTIFICATION = "pref_message_notification";
	public static final String PREF_NEW_DCARDER_NOTIFICATION = "pref_new_dcarder_notification";
	public static final String PREF_CHECK_DCARD_NOTIFICATION = "pref_check_dcard_notification";
	public static final String PREF_HOT_POST_NOTIFICATION = "pref_hot_post_notification";
	public static final String PREF_SOUND_NOTIFICATION = "pref_sound_notification";
	public static final String PREF_VIBRATION_NOTIFICATION = "pref_vibration_notification";
	public static final String PREF_CARD_GAME_BEST = "pref_card_game_best";
	public static final String PREF_STORED_MESSAGE_PREFIX = "stored_message_";
	public static final String PREF_STORED_POST_TITLE = "stored_post_title";
	public static final String PREF_STORED_POST_CONTENT = "stored_post_content";
	public static final String PREF_STORED_COMMENT = "stored_comment";
	public static final String PREF_INTRO_3 = "pref_intro_3";
	public static final String PREF_AUTO_LOAD_PIC = "pref_auto_load_pic";

	// AWS
	public static final String  S3_BUCKET = "dcard-photo";
	public static final String  S3_ACCESS_KEY_ID = "AKIAJS2S5BL7OVHG5YJA";
	public static final String  S3R_REGION = "s3-ap-northeast-1";

	// Activity request code
	public static final int REQUEST_LOGIN = 100;
	public static final int REQUEST_NETWORK_WIFI = 102;
	public static final int REQUEST_NETWORK_MOBILE = 103;
	public static final int REQUEST_GUEST = 105;
	public static final int REQUEST_WRITE = 106;
	public static final int REQUEST_UPDATE_PIC = 107;

	// Dcard Type
	public static final int DCARD_TYPE_MEMBER = 0;
	public static final int DCARD_TYPE_TODAY = 1;
	public static final int DCARD_TYPE_FRIEND = 2;
	public static final String[] DCARD_TYPE_NAMES = {"member", "today", "friend"};

	// Bundle
	public static final String WRITE_TYPE = "write_type";
	public static final int WRITE_TYPE_CREATE_POST = 1;
	public static final int WRITE_TYPE_EDIT_POST = 2;
	public static final int WRITE_TYPE_CREATE_COMMENT = 3;
	public static final int WRITE_TYPE_EDIT_COMMENT = 4;
	public static final int WRITE_TYPE_CREATE_MESSAGE = 5;

	// Write
	public static final String WRITE_ANONYMOUS_MODE = "write_anonymous_mode";
	/**
	 * Hide the anonymous CheckBox.
	 */
	public static final int WRITE_ANONYMOUS_MODE_DISABLED = 1;
	/**
	 * While CheckBox checked, hide school.
	 */
	public static final int WRITE_ANONYMOUS_MODE_HIDE_SCHOOL = 2;
	/**
	 * While CheckBox checked, hide school and department.
	 */
	public static final int WRITE_ANONYMOUS_MODE_HIDE_DEPARTMENT = 3;

	public static final String WRITE_ANONYMOUS_RESULT = "write_anonymous_result";
	public static final String WRITE_POST_ID = "write_post_id";
	public static final String WRITE_COMMENT_ID = "write_comment_id";
	public static final String WRITE_RECIEVER_ID = "write_reciever_id";
	public static final String WRITE_TITLE = "write_title";
	public static final String WRITE_CONTENT = "write_content";
	public static final String WRITE_RESULT_POSITION = "write_result_position";
	public static final String WRITE_FORUM_NAME = "write_forum_name";
	public static final String WRITE_FORUM_ALIAS = "write_forum_alias";
	public static final String WRITE_ANSWER_PAGE = "write_answer_page";

	// Overlaying fragment tags
	public static final String FRAG_TAG_MESSAGE = "MessageFragment";
	public static final String FRAG_TAG_ANSWER = "AnswerListFragment";
	public static final String FRAG_TAG_NOTIFICATION = "NotificationFragment";
}
