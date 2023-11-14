package de.xorg.gsapp.res

import dev.icerock.moko.resources.AssetResource
import dev.icerock.moko.resources.ColorResource
import dev.icerock.moko.resources.FileResource
import dev.icerock.moko.resources.FontResource
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.PluralsResource
import dev.icerock.moko.resources.ResourceContainer
import dev.icerock.moko.resources.StringResource

public expect object MR {
  public object strings : ResourceContainer<StringResource> {
    public val jan: StringResource

    public val feb: StringResource

    public val mar: StringResource

    public val apr: StringResource

    public val may: StringResource

    public val jun: StringResource

    public val jul: StringResource

    public val aug: StringResource

    public val sep: StringResource

    public val oct: StringResource

    public val nov: StringResource

    public val dec: StringResource

    public val wd_mo_lo: StringResource

    public val wd_tu_lo: StringResource

    public val wd_we_lo: StringResource

    public val wd_th_lo: StringResource

    public val wd_fr_lo: StringResource

    public val wd_sa_lo: StringResource

    public val wd_su_lo: StringResource

    public val wd_mo_sh: StringResource

    public val wd_tu_sh: StringResource

    public val wd_we_sh: StringResource

    public val wd_th_sh: StringResource

    public val wd_fr_sh: StringResource

    public val wd_sa_sh: StringResource

    public val wd_su_sh: StringResource

    public val rel_today: StringResource

    public val rel_tomorrow: StringResource

    public val rel_after_tomorrow: StringResource

    public val rel_next_weekday: StringResource

    public val rel_absolute: StringResource

    public val date_format: StringResource

    public val app_name: StringResource

    public val tab_substitutions: StringResource

    public val tab_foodplan: StringResource

    public val tab_exams: StringResource

    public val subplan_date_header_fmt: StringResource

    public val subplan_date_header_for: StringResource

    public val subplan_date_header_new: StringResource

    public val subplan_workorder_samesubject: StringResource

    public val subplan_workorder: StringResource

    public val subplan_cancellation: StringResource

    public val subplan_breastfeed: StringResource

    public val subplan_samesubject: StringResource

    public val subplan_normal: StringResource

    public val subplan_dsc_location: StringResource

    public val subplan_dsc_teacher: StringResource

    public val subplan_empty: StringResource

    public val foodplan_menu_no: StringResource

    public val foodplan_empty: StringResource

    public val examplan_empty: StringResource

    public val examplan_course_eleven: StringResource

    public val examplan_course_twelve: StringResource

    public val settings_title: StringResource

    public val pref_filter: StringResource

    public val pref_filter_teacher: StringResource

    public val pref_filter_student: StringResource

    public val pref_filter_all: StringResource

    public val pref_push: StringResource

    public val pref_subjects: StringResource

    public val pref_subjects_desc: StringResource

    public val filter_dialog_title: StringResource

    public val filter_dialog_description: StringResource

    public val filter_dialog_teacher_short: StringResource

    public val filter_all: StringResource

    public val filter_student: StringResource

    public val filter_teacher: StringResource

    public val filter_dialog_error_no_teachers: StringResource

    public val filter_dialog_error_teachers_failed: StringResource

    public val subject_manager_subjects: StringResource

    public val subject_manager_empty_title: StringResource

    public val subject_manager_empty_text: StringResource

    public val subject_manager_colorpicker_title: StringResource

    public val subject_manager_colorpicker_nullsubject: StringResource

    public val subject_manager_add_title: StringResource

    public val subject_manager_add_desc: StringResource

    public val subject_manager_restore_defaults: StringResource

    public val subject_manager_reset_dialog_title: StringResource

    public val subject_manager_reset_dialog_text: StringResource

    public val subject_manager_reset_dialog_replace: StringResource

    public val subject_manager_reset_dialog_adddefault: StringResource

    public val push_disabled: StringResource

    public val push_filter: StringResource

    public val push_enabled: StringResource

    public val push_dialog_title: StringResource

    public val push_dialog_description: StringResource

    public val push_unavailable: StringResource

    public val push_channel_name: StringResource

    public val push_channel_desc: StringResource

    public val push_notification_title: StringResource

    public val push_notification_body: StringResource

    public val push_notification_detail_amount: StringResource

    public val push_no_permission_title: StringResource

    public val push_no_permission_body: StringResource

    public val push_no_permission_fix: StringResource

    public val push_no_permission_later: StringResource

    public val push_enabled_success: StringResource

    public val push_enabled_failure: StringResource

    public val push_disabled_success: StringResource

    public val push_disabled_failure: StringResource

    public val dialog_save: StringResource

    public val dialog_cancel: StringResource

    public val dialog_color_simple: StringResource

    public val dialog_color_simple_desc: StringResource

    public val dialog_color_advanced: StringResource

    public val dialog_color_advanced_desc: StringResource

    public val dialog_color_hex: StringResource

    public val dialog_delete_title: StringResource

    public val dialog_delete_text: StringResource

    public val dialog_delete_confirm: StringResource

    public val generic_loading: StringResource

    public val generic_error_null: StringResource

    public val empty_local: StringResource

    public val back: StringResource

    public val failed_to_load: StringResource

    public val failed_send_dev: StringResource

    public val unknown_cause: StringResource

    public val subject_de: StringResource

    public val subject_ma: StringResource

    public val subject_mu: StringResource

    public val subject_ku: StringResource

    public val subject_gg: StringResource

    public val subject_re: StringResource

    public val subject_et: StringResource

    public val subject_mnt: StringResource

    public val subject_en: StringResource

    public val subject_sp: StringResource

    public val subject_spj: StringResource

    public val subject_spm: StringResource

    public val subject_bi: StringResource

    public val subject_ch: StringResource

    public val subject_ph: StringResource

    public val subject_sk: StringResource

    public val subject_if: StringResource

    public val subject_wr: StringResource

    public val subject_ge: StringResource

    public val subject_fr: StringResource

    public val subject_ru: StringResource

    public val subject_la: StringResource

    public val subject_gewi: StringResource

    public val subject_dg: StringResource

    public val subject_sn: StringResource

    public val subject_as: StringResource

    public val subject_nwut: StringResource

    public val subject_none: StringResource
  }

  public object plurals : ResourceContainer<PluralsResource>

  public object images : ResourceContainer<ImageResource> {
    public val class_lesson: ImageResource

    public val course_eleven: ImageResource

    public val course_twelve: ImageResource

    public val exams: ImageResource

    public val feedback: ImageResource

    public val filter: ImageResource

    public val filter_value: ImageResource

    public val foodplan: ImageResource

    public val groups: ImageResource

    public val original_subject: ImageResource

    public val replacement_subject: ImageResource

    public val reset: ImageResource

    public val subjects: ImageResource

    public val substitutions: ImageResource
  }

  public object fonts : ResourceContainer<FontResource> {
    public object LondrinaSolid {
      public val black: FontResource

      public val light: FontResource

      public val regular: FontResource

      public val thin: FontResource
    }

    public object OrelegaOne {
      public val regular: FontResource
    }
  }

  public object files : ResourceContainer<FileResource>

  public object colors : ResourceContainer<ColorResource>

  public object assets : ResourceContainer<AssetResource>
}
