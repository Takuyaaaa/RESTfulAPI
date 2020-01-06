package com.example.restfulapi.form;

import lombok.Data;

import javax.validation.GroupSequence;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * DateFormクラス
 *
 * @author Natsume Takuya
 */
@Data
public class DateForm {

  @NotEmpty(message = "{error.apilog.startdate.empty}", groups = IsEmpty.class)
  @Pattern(
      regexp = "[0-9]{4}-[0-9]{2}-[0-9]{2}",
      message = "{error.apilog.startdate.format}",
      groups = IsFormatValid.class)
  String startDate;

  @NotEmpty(message = "{error.apilog.enddate.empty}", groups = IsEmpty.class)
  @Pattern(
      regexp = "[0-9]{4}-[0-9]{2}-[0-9]{2}",
      message = "{error.apilog.enddate.format}",
      groups = IsFormatValid.class)
  String endDate;

  @AssertTrue(message = "{error.apilog.endbeforestart}", groups = IsDateValid.class)
  public boolean isDateValid() {
    int result = startDate.compareTo(endDate);
    return result < 0;
  }

  interface IsEmpty {}

  interface IsFormatValid {}

  interface IsDateValid {}

  @GroupSequence({IsDateValid.class, IsEmpty.class, IsFormatValid.class})
  public interface All {}
}
