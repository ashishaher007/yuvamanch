package com.ymanch.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomResponseModel<T> {
	private List<T> details; // List of dynamic details
	private int totalPages; // Total number of pages
	private int currentPage; // Current page number
	private long totalElements; // Total number of elements
	private int pageSize; // Number of elements per page
	private boolean hasNextPage; // Indicates if there is a next page
	private int nextPageNo; // Next page number

}
