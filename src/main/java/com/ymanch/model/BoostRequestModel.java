package com.ymanch.model;


import java.util.List;

import com.ymanch.helper.Enums.Status;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoostRequestModel {
    
	    private String startDate;  
	    private String startTime;  
	    private String endDate;  // This should be set to a default value if not provided
	    private String endTime;  // This should be set to a default value if not provided

	    @Enumerated(EnumType.STRING)
	    private Status status;  // Optional, can be ACTIVE or ENDED

	    @NotEmpty(message = "District IDs cannot be empty")
	    private List<Long> districtIds;

	   
	}
