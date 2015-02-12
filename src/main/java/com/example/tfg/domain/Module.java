package com.example.tfg.domain;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.example.tfg.domain.info.ModuleInfo;

@Entity
@Table(name = "module")
public class Module {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_module")
	
	private Long id;
	@Embedded
	private ModuleInfo info;
	
	@Column(name = "isDeleted", nullable = false, columnDefinition = "boolean default false")
	private Boolean isDeleted;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_degree")
	private Degree degree;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "module")
	// , cascade= CascadeType.ALL)//, orphanRemoval=true)
	private Collection<Topic> topics;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ModuleInfo getInfo() {
		return info;
	}

	public void setInfo(ModuleInfo info) {
		this.info = info;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Degree getDegree() {
		return degree;
	}

	public void setDegree(Degree degree) {
		this.degree = degree;
	}

	public Collection<Topic> getTopics() {
		return topics;
	}

	public void setTopics(Collection<Topic> topics) {
		this.topics = topics;
	}
	
	
}
