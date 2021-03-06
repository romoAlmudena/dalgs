/**
 * This file is part of D.A.L.G.S.
 *
 * D.A.L.G.S is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * D.A.L.G.S is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with D.A.L.G.S.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.ucm.fdi.dalgs.learningGoal.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.ucm.fdi.dalgs.acl.service.AclObjectService;
import es.ucm.fdi.dalgs.activity.service.ActivityService;
import es.ucm.fdi.dalgs.classes.ResultClass;
import es.ucm.fdi.dalgs.competence.service.CompetenceService;
import es.ucm.fdi.dalgs.domain.Activity;
import es.ucm.fdi.dalgs.domain.Competence;
import es.ucm.fdi.dalgs.domain.LearningGoal;
import es.ucm.fdi.dalgs.learningGoal.repository.LearningGoalRepository;

@Service
public class LearningGoalService {

	@Autowired
	CompetenceService serviceCompetence;

	@Autowired
	LearningGoalRepository repositoryLearningGoal;

	@Autowired
	ActivityService serviceActivity;

	@Autowired
	private AclObjectService manageAclService;

	@Autowired
	private MessageSource messageSource;

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Transactional(readOnly = false)
	public ResultClass<LearningGoal> addLearningGoal(LearningGoal learningGoal,
			Long id_competence, Long id_degree, Locale locale) {

		boolean success = false;

		LearningGoal learningExists = repositoryLearningGoal.existByCode(learningGoal
				.getInfo().getCode());
		ResultClass<LearningGoal> result = new ResultClass<>();

		if (learningExists != null) {
			result.setHasErrors(true);
			Collection<String> errors = new ArrayList<>();
			errors.add(messageSource.getMessage("error.Code", null, locale));

			if (learningExists.getIsDeleted()) {
				result.setElementDeleted(true);
				errors.add(messageSource.getMessage("error.deleted", null,
						locale));
				result.setSingleElement(learningExists);
			} else
				result.setSingleElement(learningGoal);
			result.setErrorsList(errors);
		} else {
			learningGoal.setCompetence(serviceCompetence.getCompetence(
					id_competence, id_degree).getSingleElement());
			success = repositoryLearningGoal.addLearningGoal(learningGoal);
			if (success) {
				learningExists = repositoryLearningGoal.existByCode(learningGoal
						.getInfo().getCode());
				success = manageAclService.addACLToObject(learningExists
						.getId(), learningExists.getClass().getName());
				if (success)
					result.setSingleElement(learningGoal);

			} else {
				throw new IllegalArgumentException(
						"Cannot create ACL. Object not set.");

			}
		}
		return result;
	}

	@PreAuthorize("isAuthenticated()")
	@Transactional(readOnly = true)
	public ResultClass<LearningGoal> getLearningGoal(Long id_learningGoal,
			Long id_competence, Long id_degree) {
		ResultClass<LearningGoal> result = new ResultClass<>();
		if (id_competence == null && id_degree == null)
			result.setSingleElement(repositoryLearningGoal
					.getLearningGoalFormatter(id_learningGoal));
		else
			result.setSingleElement(repositoryLearningGoal.getLearningGoal(
					id_learningGoal, id_competence, id_degree));
		return result;
	}

	@PreAuthorize("hasPermission(#learningGoal, 'WRITE') or hasPermission(#learningGoal, 'ADMINISTRATION')")
	@Transactional(readOnly = false)
	public ResultClass<Boolean> modifyLearningGoal(LearningGoal learningGoal,
			Long id_learningGoal, Long id_competence, Long id_degree,
			Locale locale) {
		ResultClass<Boolean> result = new ResultClass<>();

		LearningGoal modifyLearning = repositoryLearningGoal.getLearningGoal(
				id_learningGoal, id_competence, id_degree);

		LearningGoal learningExists = repositoryLearningGoal.existByCode(learningGoal
				.getInfo().getCode());

		if (!learningGoal.getInfo().getCode()
				.equalsIgnoreCase(modifyLearning.getInfo().getCode())
				&& learningExists != null) {
			result.setHasErrors(true);
			Collection<String> errors = new ArrayList<>();
			errors.add(messageSource.getMessage("error.newCode", null, locale));

			if (learningExists.getIsDeleted()) {
				result.setElementDeleted(true);
				errors.add(messageSource.getMessage("error.deleted", null,
						locale));

			}
			result.setErrorsList(errors);
			result.setSingleElement(false);
		} else {
			modifyLearning.setInfo(learningGoal.getInfo());
			boolean r = repositoryLearningGoal.saveLearningGoal(modifyLearning);
			if (r)
				result.setSingleElement(true);
		}
		return result;

	}

	@PreAuthorize("hasPermission(#learningGoal, 'DELETE') or hasPermission(#learningGoal, 'ADMINISTRATION')")
	@Transactional(readOnly = false)
	public ResultClass<Boolean> deleteLearningGoal(LearningGoal learningGoal) {
		ResultClass<Boolean> result = new ResultClass<>();
		result.setSingleElement(repositoryLearningGoal
				.deleteLearningGoal(learningGoal));
		return result;

	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResultClass<Boolean> deleteLearningGoalForCompetence(
			Competence competence) {
		ResultClass<Boolean> result = new ResultClass<>();
		result.setSingleElement(repositoryLearningGoal
				.deleteLearningGoalForCompetence(competence));
		return result;
	}

	@PreAuthorize("isAuthenticated()")
	@Transactional(readOnly = true)
	public ResultClass<LearningGoal> getLearningGoalsFromCourse(Long id_course,
			Activity activity) {
		ResultClass<LearningGoal> result = new ResultClass<>();
		Collection<LearningGoal> learningGoals = repositoryLearningGoal
				.getLearningGoalsFromActivity(activity);
		if (!learningGoals.isEmpty()) {
			result.addAll(repositoryLearningGoal.getLearningGoalsFromCourse(id_course,
					learningGoals));
			return result;
		}

		else {
			result.addAll(repositoryLearningGoal.getLearningGoalsFromCourse(id_course));
			return result;
		}
	}

	@PreAuthorize("isAuthenticated()")
	@Transactional(readOnly = true)
	public ResultClass<LearningGoal> getLearningGoalByName(String name) {
		ResultClass<LearningGoal> result = new ResultClass<>();
		result.setSingleElement(repositoryLearningGoal.getLearningGoalByName(name));
		return result;
	}

	@PreAuthorize("isAuthenticated()")
	public ResultClass<LearningGoal> getLearningGoalsFromCompetence(
			Competence competence, Boolean show) {
		ResultClass<LearningGoal> result = new ResultClass<>();
		result.addAll(repositoryLearningGoal.getLearningGoalsFromCompetence(
				competence, show));
		return result;
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResultClass<Boolean> deleteLearningGoalForCompetences(
			Collection<Competence> competences) {
		ResultClass<Boolean> result = new ResultClass<Boolean>();
		result.setSingleElement(repositoryLearningGoal
				.deleteLearningGoalsForCompetences(competences));
		return result;
	}

	@PreAuthorize("hasPermission(#learningGoal, 'WRITE') or hasPermission(#learningGoal, 'ADMINISTRATION')")
	@Transactional(readOnly = false)
	public ResultClass<LearningGoal> unDeleteLearningGoal(
			LearningGoal learningGoal, Locale locale) {
		LearningGoal l = repositoryLearningGoal.existByCode(learningGoal.getInfo()
				.getCode());
		ResultClass<LearningGoal> result = new ResultClass<>();
		if (l == null) {
			result.setHasErrors(true);
			Collection<String> errors = new ArrayList<>();
			errors.add(messageSource.getMessage("error.ElementNoExists", null,
					locale));
			result.setErrorsList(errors);

		} else {
			if (!l.getIsDeleted()) {
				Collection<String> errors = new ArrayList<>();
				errors.add(messageSource.getMessage("error.CodeNoDeleted",
						null, locale));
				result.setErrorsList(errors);
			}

			l.setDeleted(false);
			l.setInfo(learningGoal.getInfo());
			boolean r = repositoryLearningGoal.saveLearningGoal(l);
			if (r)
				result.setSingleElement(l);

		}
		return result;
	}

}
