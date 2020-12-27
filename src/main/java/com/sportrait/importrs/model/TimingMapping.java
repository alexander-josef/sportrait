/*
 * Sportrait Import API
 * Initial Sportrait Import API description - an API to process events and photo imports on the Sportrait Server 
 *
 * OpenAPI spec version: 0.0.1
 * Contact: info@sportrait.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package com.sportrait.importrs.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.File;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * Represents the data for a timing mapping of an album
 */
@Schema(description = "Represents the data for a timing mapping of an album")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2020-12-27T16:10:38.199Z[GMT]")public class TimingMapping   {
  @JsonProperty("timingFile")
  private File timingFile = null;

  @JsonProperty("differencePhotoTiming")
  private Integer differencePhotoTiming = null;

  @JsonProperty("toleranceSlowFast")
  private Integer toleranceSlowFast = null;

  @JsonProperty("photoPointAfterTiming")
  private Boolean photoPointAfterTiming = false;

  public TimingMapping timingFile(File timingFile) {
    this.timingFile = timingFile;
    return this;
  }

  /**
   * Get timingFile
   * @return timingFile
   **/
  @JsonProperty("timingFile")
  @Schema(required = true, description = "")
  @NotNull
  @Valid
  public File getTimingFile() {
    return timingFile;
  }

  public void setTimingFile(File timingFile) {
    this.timingFile = timingFile;
  }

  public TimingMapping differencePhotoTiming(Integer differencePhotoTiming) {
    this.differencePhotoTiming = differencePhotoTiming;
    return this;
  }

  /**
   * Example 20s. It took an average runner 20 seconds from photo point to timing 
   * @return differencePhotoTiming
   **/
  @JsonProperty("differencePhotoTiming")
  @Schema(example = "20", required = true, description = "Example 20s. It took an average runner 20 seconds from photo point to timing ")
  @NotNull
  public Integer getDifferencePhotoTiming() {
    return differencePhotoTiming;
  }

  public void setDifferencePhotoTiming(Integer differencePhotoTiming) {
    this.differencePhotoTiming = differencePhotoTiming;
  }

  public TimingMapping toleranceSlowFast(Integer toleranceSlowFast) {
    this.toleranceSlowFast = toleranceSlowFast;
    return this;
  }

  /**
   * Example 10s. 10 seconds are added as tolerance on both sides. I.e. a slow runner with 29 seconds still is mapped, as well as a fast runner with 11 seconds (check if this is true or if it&#x27;s toleranceSlowFast/2 ) 
   * @return toleranceSlowFast
   **/
  @JsonProperty("toleranceSlowFast")
  @Schema(example = "10", required = true, description = "Example 10s. 10 seconds are added as tolerance on both sides. I.e. a slow runner with 29 seconds still is mapped, as well as a fast runner with 11 seconds (check if this is true or if it's toleranceSlowFast/2 ) ")
  @NotNull
  public Integer getToleranceSlowFast() {
    return toleranceSlowFast;
  }

  public void setToleranceSlowFast(Integer toleranceSlowFast) {
    this.toleranceSlowFast = toleranceSlowFast;
  }

  public TimingMapping photoPointAfterTiming(Boolean photoPointAfterTiming) {
    this.photoPointAfterTiming = photoPointAfterTiming;
    return this;
  }

  /**
   * Was the photo point located after the timing? (can be left out, defaults to false)
   * @return photoPointAfterTiming
   **/
  @JsonProperty("photoPointAfterTiming")
  @Schema(description = "Was the photo point located after the timing? (can be left out, defaults to false)")
  public Boolean isPhotoPointAfterTiming() {
    return photoPointAfterTiming;
  }

  public void setPhotoPointAfterTiming(Boolean photoPointAfterTiming) {
    this.photoPointAfterTiming = photoPointAfterTiming;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TimingMapping timingMapping = (TimingMapping) o;
    return Objects.equals(this.timingFile, timingMapping.timingFile) &&
        Objects.equals(this.differencePhotoTiming, timingMapping.differencePhotoTiming) &&
        Objects.equals(this.toleranceSlowFast, timingMapping.toleranceSlowFast) &&
        Objects.equals(this.photoPointAfterTiming, timingMapping.photoPointAfterTiming);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timingFile, differencePhotoTiming, toleranceSlowFast, photoPointAfterTiming);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TimingMapping {\n");
    
    sb.append("    timingFile: ").append(toIndentedString(timingFile)).append("\n");
    sb.append("    differencePhotoTiming: ").append(toIndentedString(differencePhotoTiming)).append("\n");
    sb.append("    toleranceSlowFast: ").append(toIndentedString(toleranceSlowFast)).append("\n");
    sb.append("    photoPointAfterTiming: ").append(toIndentedString(photoPointAfterTiming)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
