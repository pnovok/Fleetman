package com.virtualpairprogrammers.tracker.data;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.core.convert.CouchbaseConverter;
import org.springframework.data.couchbase.core.convert.CouchbaseCustomConversions;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;
import org.springframework.data.couchbase.repository.support.IndexManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableCouchbaseRepositories
public class CustomCouchbaseConfig extends AbstractCouchbaseConfiguration {

    @Override
    protected List<String> getBootstrapHosts() {
        return Collections.singletonList("localhost");
    }

    @Override
    protected String getBucketName() {
        return "test";
    }

    @Override
    protected String getBucketPassword() {
        return "couchbase";
    }

        @Override
    public CouchbaseCustomConversions customConversions() {
       return new CouchbaseCustomConversions(Arrays.asList(BigDecimalToString.INSTANCE, StringToBigDecimalConverter.INSTANCE));
    }

    @WritingConverter
    public enum BigDecimalToString implements Converter<BigDecimal, String> {
        INSTANCE;
        @Override
        public String convert(BigDecimal source) {
            return source.toString() ;
        }

       }
    @ReadingConverter
    public enum StringToBigDecimalConverter implements Converter<String, BigDecimal> {
        INSTANCE;

        @Override
        public BigDecimal convert(String source) {
            return new BigDecimal(source);
        }

     }

}
