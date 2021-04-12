package com.wander.swplanet.domain;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@With
@Table("planet")
public class Planet {
	
	@Id
	private Integer id;
	@NotNull
	@NotEmpty(message = "The name of planet is mandatory")
	private String name;
	private String climate;
	private String terrain;
	private Long filmAppearences;
	
}
