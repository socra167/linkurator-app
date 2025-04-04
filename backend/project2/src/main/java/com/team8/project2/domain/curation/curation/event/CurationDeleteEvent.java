package com.team8.project2.domain.curation.curation.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CurationDeleteEvent {
	private Long curationId;
}
