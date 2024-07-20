package com.service_health_monitor_portal.log_analyzer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;

import okhttp3.MediaType;


public class influxDBControllerTest {
private MockMvc mockMvc;

@Mock
private InfluxDBService influxDBService;

@InjectMocks
private InfluxDBController influxDBController;

@BeforeEach
public void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(influxDBController).build();
}


// Test for writeData method
 
 @Test
 public void testWriteData_Success() throws Exception {
     
     when(influxDBService.singlePointWrite(any(Point.class))).thenReturn(true);// its tell to mock influxDBService to return true when singlePointWrite is called with any Point object

     mockMvc.perform(post("/influxdb/write")) // this will simulate the post request to /influxdb/write
             .andExpect(status().isOk())
             .andExpect(content().string("Write Successful"));

     verify(influxDBService).singlePointWrite(any(Point.class));
 }
    
    @Test
    public void testWriteData_Failure() throws Exception {

        when(influxDBService.singlePointWrite(any(Point.class))).thenReturn(false);

        mockMvc.perform(post("/influxdb/write"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Write Failed"));

        verify(influxDBService).singlePointWrite(any(Point.class));// this confirms that singlePointWrite is called with any Point object
    }

 // Test for queryData method

    @Test
    public void testQueryData_Success() throws Exception {

        FluxTable mockTable = new FluxTable(); // create a mock object of FluxTable
        when(influxDBService.queryData(anyString())).thenReturn(Collections.singletonList(mockTable)); // its tell to mock influxDBService to return a list containing mockTable object when queryData is called with any string

        mockMvc.perform(get("/influxdb/query"))
                .andExpect(status().isOk());

        verify(influxDBService).queryData(anyString());
    }

    @Test
    public void testQueryData_Exception() throws Exception {

        when(influxDBService.queryData(anyString())).thenThrow(new RuntimeException("Test Exception"));
        mockMvc.perform(get("/influxdb/query"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("[]"));

        verify(influxDBService).queryData(anyString());
    }
    
    
   // Test for getAllServices method

    @Test
    public void testGetAllServices_Success() throws Exception {
        // mock data
        FluxTable mockTable = new FluxTable();
        FluxRecord mockRecord = new FluxRecord(0);
        mockRecord.getValues().put("name", "Test Service");
        mockRecord.getValues().put("id", "123");
        mockTable.getRecords().add(mockRecord);
        when(influxDBService.queryData(anyString())).thenReturn(Collections.singletonList(mockTable));
        mockMvc.perform(get("/influxdb/services"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"name\":\"Test Service\",\"id\":\"123\"}]"));
        verify(influxDBService).queryData(anyString());

    }
    
    @Test
    public void testGetAllServices_Exception() throws Exception {

        when(influxDBService.queryData(anyString())).thenThrow(new RuntimeException("Test Exception"));
        mockMvc.perform(get("/influxdb/services"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("[]"));

        verify(influxDBService).queryData(anyString());
    }



    // Test for getServiceDataById method
    

    
 

   
}
