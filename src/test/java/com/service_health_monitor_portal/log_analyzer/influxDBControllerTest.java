package com.service_health_monitor_portal.log_analyzer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.influxdb.client.write.Point;
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

 @Test
 public void testWriteData_Success() throws Exception {
     
     when(influxDBService.singlePointWrite(any(Point.class))).thenReturn(true);

     mockMvc.perform(post("/influxdb/write"))
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

        verify(influxDBService).singlePointWrite(any(Point.class));
    }

    
 

   
}
