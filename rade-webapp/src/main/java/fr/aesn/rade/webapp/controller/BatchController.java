/*  This file is part of the Rade project (https://github.com/mgimpel/rade).
 *  Copyright (C) 2018 Marc Gimpel
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
/* $Id$ */
package fr.aesn.rade.webapp.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.concurrent.ForkJoinPool;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import fr.aesn.rade.webapp.model.SearchEntite;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring MVC Controller for Rade Batch processing.
 * @author Marc Gimpel (mgimpel@gmail.com)
 */
@Slf4j
@Controller
@RequestMapping(BatchController.REQUEST_MAPPING)
public class BatchController {
  /** RequestMapping for this Controller. */
  public static final String REQUEST_MAPPING = "/batch";
  /** Spring Batch Job Launcher. */
  @Autowired
  private JobLauncher jobLauncher;
  /** Batch Info Job. */
  @Autowired
  @Qualifier("infoJob")
  private Job infoJob;
  /** Batch Import Communes Sandre Job. */
  @Autowired
  @Qualifier("importCommuneSandreJob")
  private Job sandreJob;

  /**
   * Info Batch Upload mapping.
   * @param model MVC model passed to JSP.
   * @return View for the Info Batch Upload page
   */
  @RequestMapping(value = "/info", method = RequestMethod.GET)
  public String infoGet(final Model model) {
    log.debug("Requesting /batch/info");
    model.addAttribute("titre", "Batch Info");
    model.addAttribute("entite", new SearchEntite());
    return "batchinfo";
  }

  /**
   * Sandre Batch Upload mapping.
   * @param model MVC model passed to JSP.
   * @return View for the Sandre Batch Upload page
   */
  @RequestMapping(value = "/sandre", method = RequestMethod.GET)
  public String sandreGet(final Model model) {
    log.debug("Requesting /batch/sandre");
    model.addAttribute("titre", "Batch Sandre");
    model.addAttribute("entite", new SearchEntite());
    return "batchsandre";
  }

  /**
   * Upload file and run Info batch.
   * @param model MVC model passed to JSP.
   * @param file the HTTP submitted file. 
   * @return View for the page.
   * @throws IOException if there was a problem recovering and saving file.
   */
  @RequestMapping(value = "/info", method = RequestMethod.POST)
  public String infoPost(final Model model,
                         @RequestParam("file") final MultipartFile file)
    throws IOException {
    log.debug("Posting to /batch/info");
    model.addAttribute("titre", "Batch Info");
    model.addAttribute("entite", new SearchEntite());
    Path tmpFile = storeTempFile(file);
    JobParametersBuilder jobBuilder = new JobParametersBuilder();
    jobBuilder.addString("inputFile", tmpFile.toUri().toString());
    jobBuilder.addString("auditAuteur", "WebBatch");
    jobBuilder.addDate("auditDate", new Date());
    jobBuilder.addString("auditNote", "Import " + file.getOriginalFilename());
    model.addAttribute("file", file);
    model.addAttribute("uri", tmpFile.toUri());
    try {
      JobExecution exec = jobLauncher.run(infoJob, jobBuilder.toJobParameters());
      log.info("Info job completed: {}", exec);
    } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
      log.warn("Exception running Info Job", e);
    }
    return "batchinforesult";
  }

  /**
   * Upload file and run Sandre import batch.
   * @param model MVC model passed to JSP.
   * @param file the HTTP submitted file. 
   * @return View for the page.
   * @throws IOException if there was a problem recovering and saving file.
   */
  @RequestMapping(value = "/sandre", method = RequestMethod.POST)
  public DeferredResult<String> sandrePost(final Model model,
                                           @RequestParam("file") final MultipartFile file)
    throws IOException {
    log.debug("Posting to /batch/sandre");
    model.addAttribute("titre", "Batch Sandre");
    model.addAttribute("entite", new SearchEntite());
    Path tmpFile = storeTempFile(file);
    JobParametersBuilder jobBuilder = new JobParametersBuilder();
    jobBuilder.addString("inputFile", tmpFile.toUri().toString());
    jobBuilder.addString("auditAuteur", "WebBatch");
    jobBuilder.addDate("auditDate", new Date());
    jobBuilder.addString("auditNote", "Import " + file.getOriginalFilename());
    model.addAttribute("file", file);
    model.addAttribute("uri", tmpFile.toUri());
    DeferredResult<String> deferredResult = new DeferredResult<>();
    ForkJoinPool.commonPool().submit(() -> {
      deferredResult.setResult("batchsandreresult");
      try {
        JobExecution exec = jobLauncher.run(sandreJob, jobBuilder.toJobParameters());
        log.info("Sandre import completed: {}", exec);
      } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
        log.warn("Exception running Sandre Job", e);
      }
    });
    return deferredResult;
  }

  /**
   * Store the HTTP submitted file in a temporary location.
   * @param file the HTTP submitted file. 
   * @return the path to the temporary file.
   * @throws IOException if there was a problem recovering and saving file.
   */
  private Path storeTempFile(final MultipartFile file)
    throws IOException {
    Path tmpDir = Paths.get(System.getProperty("java.io.tmpdir")
                          + File.separator + "rade" + File.separator
                          + "uploads" + File.separator)
                       .toAbsolutePath()
                       .normalize();
    if (!Files.exists(tmpDir)) {
      log.info("Creating Temporary Directory: {}", tmpDir);
      Files.createDirectories(tmpDir);
    }
    Path tmpFile = Files.createTempFile(tmpDir, file.getOriginalFilename(), null);
    Files.copy(file.getInputStream(), tmpFile, StandardCopyOption.REPLACE_EXISTING);
    return tmpFile;
  }
}
