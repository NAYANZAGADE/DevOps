package com.glidingpath.platform.sponsor.controllers;

import com.glidingpath.platform.sponsor.dto.PlanTypeDTO;
import com.glidingpath.platform.sponsor.service.PlanTypeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/plan-sponsor/plan/types")
@RequiredArgsConstructor
@Tag(name = "Plan-Sponsor Details", description = "APIs for retrieving plan types and configurations")
public class PlanTypeController {

    private final PlanTypeService planTypeService;

    /**
     * Endpoint to retrieve all available plan types.
     *
     * @return ResponseEntity with List of PlanTypeDTO containing all plan types
     */
    @GetMapping
    @Operation(summary = "Get all plan types", description = "Retrieves all plan types and their configurations")
    public List<PlanTypeDTO> getAllPlanTypes() {
        return planTypeService.getAllPlanTypes();
    }
} 