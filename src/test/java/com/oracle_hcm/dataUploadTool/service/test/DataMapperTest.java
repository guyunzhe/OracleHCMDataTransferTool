package com.oracle_hcm.dataUploadTool.service.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.oracle_hcm.dataUploadTool.service.DataMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:service.xml"})
public class DataMapperTest {

	@Autowired
	private DataMapper dataMapper;

	@Test
	public void test() {
		this.dataMapper.mapData();
	}

}
