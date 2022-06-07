package com.chs.sample.controller;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.BreakIterator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.chs.sample.message.ResponseMessage;
import com.chs.sample.model.Customer;
import com.chs.sample.model.FileInfo;
import com.chs.sample.repository.CustomerRepository;
import com.chs.sample.service.FilesStorageService;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;


@EnableScheduling
@RestController
@CrossOrigin("http://localhost:8081")

@RequestMapping("/load")
public class LoadController {

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	CustomerRepository service;

	@Autowired
	Job job;

	@Autowired
	FilesStorageService storageService;

	@GetMapping
	public BatchStatus load() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {

		Map<String, JobParameter> maps = new HashMap<>();
		maps.put("time", new JobParameter(System.currentTimeMillis()));

		JobParameters parameters = new JobParameters(maps);
		JobExecution jobExecution = jobLauncher.run(job, parameters);

		System.out.println("JobExecution: " + jobExecution.getStatus());

		System.out.println("Batch is Running...");
		while (jobExecution.isRunning()) {
			System.out.println("...");
		}

		return jobExecution.getStatus();
	}


	@PostMapping("/import-batch")
	public BatchStatus importCustomersAsBatch(@RequestParam("file") MultipartFile file) throws Exception {
		
		String fileName1  = file.getOriginalFilename();
		String timestamp = ""+System.currentTimeMillis();
		String fileName ="Cbi"+timestamp+fileName1;

		/*
		 * if(file.isEmpty()) {
		 * 
		 * System.out.println("@@@@ Empty File : "+fileName);
		 * 
		 * 
		 * }else { System.out.println("@@@@ File has Data : "+fileName);
		 * 
		 * }
		 */
		System.out.println("@@@@ Input File Name : "+fileName);
		Path root = Paths.get("uploads/"+timestamp);


		System.out.println("@@@@ Input root Name : "+root);
		Files.createDirectory(root);
		Files.copy(file.getInputStream(), root.resolve(file.getOriginalFilename()));
		System.out.println("@@@@ Input test Name : "+root);

		Path oldFile= Paths.get(root+"/"+fileName1);
		Files.move(oldFile, oldFile.resolveSibling(fileName));
		System.out.println("@@@@ Input old Name : "+oldFile);
		Map<String, JobParameter> maps = new HashMap<>();
		maps.put("customerFileName",new JobParameter(timestamp+"/"+fileName));
		//maps.put("customerFileName",new JobParameter(fileName));

		JobParameters params = new JobParameters(maps);
		JobExecution jobExecution = jobLauncher.run(job, params);

		System.out.println("JobExecution: " + jobExecution.getStatus());

		System.out.println("Customer Batch is Running...");
		while (jobExecution.isRunning()) {
			System.out.println("...");
		}

		return jobExecution.getStatus();
	}


	@PostMapping("/import-bulk")
	public String importCustomers(@RequestParam("file") MultipartFile file) throws Exception {

		List<Customer> customerList = new ArrayList<>();
		InputStream inputStream = file.getInputStream();
		CsvParserSettings setting = new CsvParserSettings();
		setting.setHeaderExtractionEnabled(true);
		CsvParser parser = new CsvParser(setting);
		List<Record> parseAllRecords = parser.parseAllRecords(inputStream);
		parseAllRecords.forEach(record -> {
			Customer customer = new Customer();
			customer.setId(Integer.parseInt(record.getString("id")));
			customer.setTitle(record.getString("title"));
			customer.setFirstName(record.getString("firstName"));
			customer.setMiddleName(record.getString("middleName"));
			customer.setLastName(record.getString("lastName"));
			customer.setEmailId(record.getString("emailId"));
			customer.setMobileNumber(record.getString("mobileNumber"));
			customer.setPan(record.getString("pan"));
			customer.setAadhaar(record.getString("aadhaar"));
			customerList.add(customer);

		});
		service.saveAll(customerList);
		System.out.println("IMported File Name is : " + file.getOriginalFilename());
		System.out.println("IMported File Name is : " + file.getContentType());
		System.out.println("IMported File Name is : " + file.getInputStream());

		return "upload success";
	}

	@PostMapping("/upload")
	public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
		String message = "";
		try {
			storageService.save(file);
			message = "Uploaded the file successfully: " + file.getOriginalFilename();
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
		} catch (Exception e) {
			message = "Could not upload the file: " + file.getOriginalFilename() + "!";
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
		}
	}

	@GetMapping("/files")
	public ResponseEntity<List<FileInfo>> getListFiles() {
		List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
			String filename = path.getFileName().toString();
			String url = MvcUriComponentsBuilder
					.fromMethodName(LoadController.class, "getFile", path.getFileName().toString()).build().toString();
			return new FileInfo(filename, url);
		}).collect(Collectors.toList());
		return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
	}

	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> getFile(@PathVariable String filename) {
		Resource file = storageService.load(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

	@PostMapping("/raju")
	public int add (int a,int b) {
		
		
		return a+b;
	}


	private static final Logger logger =LoggerFactory.getLogger(LoadController.class);
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");


	

	public void schedulingTaskWithCornExpression() {
		logger.info("rajuTask- {}",dateTimeFormatter.format(LocalDateTime.now()));

	}	

}
