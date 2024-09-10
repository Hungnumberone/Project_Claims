package com.spring.controller;

import com.spring.entities.Claims;
import com.spring.entities.Project;
import com.spring.entities.ProjectDetail;
import com.spring.entities.Staff;
import com.spring.reponsitory.ClaimsRepository;
import com.spring.reponsitory.ProjectDetailReponsitory;
import com.spring.reponsitory.ProjectReponsitory;
import com.spring.reponsitory.StaffReponsitory;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Time;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class UpdateController {
    @Autowired
    private ProjectReponsitory projectReponsitory;

    @Autowired
    private StaffReponsitory staffReponsitory;

    @Autowired
    private ProjectDetailReponsitory projectDetailReponsitory;
    @Autowired
    private ClaimsRepository claimsRepository;
    @GetMapping(value = {"claims/update/{id}"})
    public String claimsPage(
            @PathVariable("id") Integer id,
            Model model, HttpSession session
    ) {
       Integer idClaims = Integer.valueOf(id);

       Claims claim = claimsRepository.findById(idClaims).orElse(null);
        List<ProjectDetail> projectDetails = projectDetailReponsitory.findByProjectDetailKeyStaffId(claim.getProject().getId());
        List<Integer> ids = new ArrayList<>();

        for (ProjectDetail projectDetail : projectDetails) {
            ids.add(projectDetail.getProject().getId());
        }
        List<Project> projects = projectReponsitory.findAllById(ids);
        Map<Integer, String> projectRoles = new HashMap<>();
        for (Project project : projects) {
            String roles = projectDetails.stream()
                    .filter(pd -> pd.getProject().getId().equals(project.getId()))
                    .map(ProjectDetail::getRoleProject)
                    .collect(Collectors.joining(", "));
            projectRoles.put(project.getId(), roles);
        }
        model.addAttribute("projects",projects);
        model.addAttribute("projectRoles", projectRoles);

        Project selectedProject = projectReponsitory.findById(claim.getProject().getId()).orElse(null);
        model.addAttribute("claims",claim);
        model.addAttribute("selectedProject", selectedProject);
        return "claims/createClaims";
    }

    @GetMapping(value = {"/claims/delete/{id}"})
    public String delete(@PathVariable("id") Integer id,
                         Model model,
                         RedirectAttributes redirectAttributes
    ) {
        Optional<Claims> claim = claimsRepository.findById(id);
        if(claim.isPresent()) {
            claimsRepository.delete(claim.get());
        }
        redirectAttributes.addFlashAttribute("Delete successful");
        return "redirect:/claims/view";
    }


}
