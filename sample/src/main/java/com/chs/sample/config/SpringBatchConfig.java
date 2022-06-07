package com.chs.sample.config;

import java.io.IOException;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.chs.sample.model.Customer;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {
	
	@Autowired
	JobBuilderFactory jobBuilderFactory;
	
	@Bean
	public Job job(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
			ItemReader<Customer> itemReader, ItemProcessor<Customer, Customer> itemProcessor,
			ItemWriter<Customer> itemWriter) {

		Step step = stepBuilderFactory.get("ETL-file-load").<Customer, Customer>chunk(100).reader(itemReader)
				.processor(itemProcessor).writer(itemWriter).build();

		return jobBuilderFactory.get("ETL-Load").incrementer(new RunIdIncrementer()).start(step).build();
	}
	
	
	
	@Bean
	@StepScope
	public FlatFileItemReader<Customer> itemReader(@Value("#{jobParameters[customerFileName]}") String customerFileName) {
		FlatFileItemReader<Customer> flatFileItemReader = new FlatFileItemReader<>();
		System.out.println("@@@@@    : Imported Customer Batch File Name : "+customerFileName);
		FileSystemResource fsr = new FileSystemResource("uploads/"+customerFileName);
		try {
			System.out.println("@@@@@ File System Reousre File : "+fsr.getURL());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		flatFileItemReader.setResource(fsr); 
		flatFileItemReader.setName("CSV-Reader"); //
		flatFileItemReader.setLinesToSkip(1); //
		flatFileItemReader.setLineMapper(linelMapper());
		return flatFileItemReader;
	}

//	@Bean
//    public FlatFileItemReader<Customer> importReader(@Value("#{parameters[fullPathFileName]}") String pathToFile) {
//        FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
//        reader.setResource(new FileSystemResource(pathToFile));
//        reader.setLineMapper(linelMapper());
//        return reader;
//    }

	@Bean
	public LineMapper<Customer> linelMapper() {
		DefaultLineMapper<Customer> defaultLineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();

		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames("title", "firstName", "middleName", "lastName", "emailId",
				"mobileNumber", "pan", "aadhaar");

		BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(Customer.class);

		defaultLineMapper.setLineTokenizer(lineTokenizer);
		defaultLineMapper.setFieldSetMapper(fieldSetMapper);

		return defaultLineMapper;
	}

}
