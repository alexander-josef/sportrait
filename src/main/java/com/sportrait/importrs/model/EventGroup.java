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
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * Represents an EventGroup in Sportrait
 */
@Schema(description = "Represents an EventGroup in Sportrait")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2020-12-27T16:10:38.199Z[GMT]")public class EventGroup   {
  @JsonProperty("id")
  private Long id = null;

  @JsonProperty("events")
  private List<Event> events = null;

  @JsonProperty("title")
  private String title = null;

  @JsonProperty("description")
  private String description = null;

  /**
   * eventGroup status
   */
  public enum StatusEnum {
    NEW("new"),
    
    PUBLISHED("published"),
    
    ARCHIVED("archived");

    private String value;

    StatusEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static StatusEnum fromValue(String text) {
      for (StatusEnum b : StatusEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }
  @JsonProperty("status")
  private StatusEnum status = null;

  public EventGroup id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   **/
  @JsonProperty("id")
  @Schema(description = "")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public EventGroup events(List<Event> events) {
    this.events = events;
    return this;
  }

  public EventGroup addEventsItem(Event eventsItem) {
    if (this.events == null) {
      this.events = new ArrayList<Event>();
    }
    this.events.add(eventsItem);
    return this;
  }

  /**
   * Get events
   * @return events
   **/
  @JsonProperty("events")
  @Schema(description = "")
  @Valid
  public List<Event> getEvents() {
    return events;
  }

  public void setEvents(List<Event> events) {
    this.events = events;
  }

  public EventGroup title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
   **/
  @JsonProperty("title")
  @Schema(example = "ASVZ SOLA", required = true, description = "")
  @NotNull
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public EventGroup description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   **/
  @JsonProperty("description")
  @Schema(example = "Bilder der ASVZ SOLA Stafetten seit 2005", description = "")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public EventGroup status(StatusEnum status) {
    this.status = status;
    return this;
  }

  /**
   * eventGroup status
   * @return status
   **/
  @JsonProperty("status")
  @Schema(required = true, description = "eventGroup status")
  @NotNull
  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EventGroup eventGroup = (EventGroup) o;
    return Objects.equals(this.id, eventGroup.id) &&
        Objects.equals(this.events, eventGroup.events) &&
        Objects.equals(this.title, eventGroup.title) &&
        Objects.equals(this.description, eventGroup.description) &&
        Objects.equals(this.status, eventGroup.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, events, title, description, status);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EventGroup {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    events: ").append(toIndentedString(events)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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