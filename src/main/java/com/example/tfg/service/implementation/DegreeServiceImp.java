package com.example.tfg.service.implementation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tfg.classes.ResultClass;
import com.example.tfg.domain.AcademicTerm;
import com.example.tfg.domain.Degree;
import com.example.tfg.domain.Subject;
import com.example.tfg.repository.DegreeDao;
import com.example.tfg.service.AcademicTermService;
import com.example.tfg.service.CompetenceService;
import com.example.tfg.service.DegreeService;
import com.example.tfg.service.ModuleService;

@Service
public class DegreeServiceImp implements DegreeService {

	@Autowired
	private DegreeDao daoDegree;

	//	@Autowired
	//	private SubjectService serviceSubject;

	
	@Autowired
	private ModuleService serviceModule;

	@Autowired
	private CompetenceService serviceCompetence;

	@Autowired
	private AcademicTermService serviceAcademicTerm;

	@PreAuthorize("hasRole('ROLE_ADMIN')")	
	@Transactional(readOnly=false)
	public ResultClass<Boolean> addDegree(Degree degree) {

		Degree degreeExists = daoDegree.existByCode(degree.getInfo().getCode());
		ResultClass<Boolean> result = new ResultClass<Boolean>();

		if( degreeExists != null){
			result.setHasErrors(true);
			Collection<String> errors = new ArrayList<String>();
			errors.add("Code already exists");

			if (degreeExists.isDeleted()){
				result.setElementDeleted(true);
				errors.add("Element is deleted");

			}
			result.setErrorsList(errors);
		}
		else{
			boolean r = daoDegree.addDegree(degree);
			if (r) 
				result.setE(true);
		}
		return result;

	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@Transactional(readOnly = true)
	public List<Degree> getAll() {
		return daoDegree.getAll();
	}

	//	public boolean modifyDegree(Degree degree) {
	//
	//		return daoDegree.saveDegree(degree);
	//	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")	
	@Transactional(readOnly = false)
	public ResultClass<Boolean> modifyDegree(Degree degree, Long id_degree) {
		ResultClass<Boolean> result = new ResultClass<Boolean>();

		Degree modifydegree = daoDegree.getDegree(id_degree);
		
		Degree degreeExists = daoDegree.existByCode(degree.getInfo().getCode());
		
		if(!degree.getInfo().getCode().equalsIgnoreCase(modifydegree.getInfo().getCode()) && 
				degreeExists != null){
			result.setHasErrors(true);
			Collection<String> errors = new ArrayList<String>();
			errors.add("New code already exists");

			if (degreeExists.isDeleted()){
				result.setElementDeleted(true);
				errors.add("Element is deleted");

			}
			result.setErrorsList(errors);
		}
		else{
			modifydegree.setInfo(degree.getInfo());
			boolean r = daoDegree.saveDegree(modifydegree);
			if (r) 
				result.setE(true);
		}
		return result;

		
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@Transactional(readOnly = false)
	public Degree getDegree(Long id) {
		return daoDegree.getDegree(id);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")	
	@Transactional(readOnly = false)
	public boolean deleteDegree(Long id) {
		boolean deleteModules = false;
		boolean deleteCompetences = false;
		boolean deleteAcademic = false;

		Degree d = daoDegree.getDegree(id);
		if (!d.getModules().isEmpty())
			deleteModules = serviceModule.deleteModulesForDegree(d);
		if (!d.getCompetences().isEmpty())
			deleteCompetences = serviceCompetence.deleteCompetencesForDegree(d);
		Collection<AcademicTerm> academicList = serviceAcademicTerm.getAcademicTermsByDegree(id);

		if(!academicList.isEmpty()) deleteAcademic = serviceAcademicTerm.deleteAcademicTerm(academicList);
		if ((deleteModules || d.getModules().isEmpty()) && (deleteCompetences || d.getCompetences().isEmpty())
				&& (deleteAcademic || academicList.isEmpty())){
				
			return daoDegree.deleteDegree(d);
		} else
			return false;
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@Transactional(readOnly = true)
	public Degree getDegreeSubject(Subject p) {

		return daoDegree.getDegreeSubject(p);
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@Transactional(readOnly = true)
	public String getNextCode() {
		return daoDegree.getNextCode();

	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@Transactional(readOnly = true)
	public Degree getDegreeAll(Long id) {


		Degree d = daoDegree.getDegree(id);
		d.setModules(serviceModule.getModulesForDegree(id));
		d.setCompetences(serviceCompetence.getCompetencesForDegree(id));
		return d;
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")	
	@Transactional(readOnly = false)
	public ResultClass<Boolean> unDeleteDegree(Degree degree) {
		Degree d = daoDegree.existByCode(degree.getInfo().getCode());
		ResultClass<Boolean> result = new ResultClass<Boolean>();
		if(d == null){
			result.setHasErrors(true);
			Collection<String> errors = new ArrayList<String>();
			errors.add("Code doesn't exist");
			result.setErrorsList(errors);

		}
		else{
			if(!d.isDeleted()){
				Collection<String> errors = new ArrayList<String>();
				errors.add("Code is not deleted");
				result.setErrorsList(errors);
			}

			d.setDeleted(false);
			d.setInfo(degree.getInfo());
			daoDegree.saveDegree(d);
			result.setE(true);	

		}
		return result;
	}
}
