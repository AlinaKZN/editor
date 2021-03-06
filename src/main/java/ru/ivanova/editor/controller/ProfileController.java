package ru.ivanova.editor.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.ivanova.editor.dto.ProfileDto;
import ru.ivanova.editor.message.Messages;
import ru.ivanova.editor.model.Profile;
import ru.ivanova.editor.service.ErrorService;
import ru.ivanova.editor.service.ProfileService;
import ru.ivanova.editor.util.Utils;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(value = ProfileController.REST_URL)
public class ProfileController {
  static final String REST_URL = "/profiles";

  private ProfileService profileService;

  private ErrorService errorService;

  @PostMapping(value = "/set", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity create(@RequestBody ProfileDto profile) {
    log.info(REST_URL + "/set {}", profile);
    Profile createdProfile = profileService.create(Utils.toProfile(profile));
    return ResponseEntity.ok(createdProfile.getId());
  }

  @GetMapping(value = "/last")
  public ResponseEntity getLastProfile() {
    log.info(REST_URL + "/last");
    Profile profile = profileService.getLast();
    if (profile != null) {
      return ResponseEntity.ok(profile);
    }
    return new ResponseEntity(HttpStatus.NO_CONTENT);
  }

  @GetMapping
  public ResponseEntity getAllProfiles() {
    log.info(REST_URL);
    List<Profile> profiles = profileService.getAll();
    return ResponseEntity.ok(profiles);
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity getProfile(@PathVariable Long id) {
    log.info(REST_URL + "/{}", id);
    Optional<Profile> profile = profileService.findById(id);
    if (!profile.isPresent()) {
      errorService.log(Messages.PROFILE_WITH_ID_NOTFOUND);
      Map<String, Object> body = Map.of("msg", Messages.PROFILE_WITH_EMAIL_NOTFOUND);
      return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
    return ResponseEntity.ok(profile);
  }

  @PostMapping(value = "/get")
  public ResponseEntity getProfile(@RequestBody ProfileDto profileDto) {
    String email = profileDto.getEmail();
    log.info(REST_URL + "/get/{}", email);
    Optional<Profile> profile = profileService.findByEmail(email);
    if (!profile.isPresent()) {
      errorService.log(Messages.PROFILE_WITH_ID_NOTFOUND);
      Map<String, Object> body = Map.of("message", Messages.PROFILE_WITH_EMAIL_NOTFOUND);
      return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
    return ResponseEntity.ok(profile);
  }
}
