package com.glidingpath.platform.shared.controllers;

import com.glidingpath.platform.shared.dto.ParticipantResponseDTO;
import com.glidingpath.platform.shared.dto.ParticipantSearchRequestDTO;
import com.glidingpath.platform.shared.service.ParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.glidingpath.auth.security.CurrentUser;
import com.glidingpath.core.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/participants")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Participants", description = "APIs for managing participant data and employee information")
public class ParticipantController {
    
    private final ParticipantService participantService;
    
    /**
     * Endpoint to retrieve paginated participant data with optional search functionality.
     *
     * @param page page number (0-based)
     * @param size number of records per page
     * @param search optional search term for participant names
     * @param tenantId tenant identifier
     * @return ResponseEntity with Page of ParticipantResponseDTO containing participant data
     */
    @GetMapping
    @Operation(
        summary = "Get participants with pagination and search", 
        description = "Retrieves paginated list of participants with optional name search functionality. " +
                     "Supports searching by first name or last name with case-insensitive matching. " +
                     "Returns participant data including account number, full name, employment status, " +
                     "portfolio type, balance, and vesting status.",
        operationId = "getParticipants"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Participants retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Page.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(example = "Invalid tenant ID or pagination parameters")
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(example = "Error retrieving participant data")
            )
        )
    })
    public ResponseEntity<Page<ParticipantResponseDTO>> getParticipants(
            @CurrentUser User user,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Optional search term for participant names (case-insensitive)")
            @RequestParam(required = false) String search) {
                String tenantId = user.getTenantId();
        log.info("Received participant request - page: {}, size: {}, search: {}, tenantId: {}", 
                page, size, search, tenantId);
        
        ParticipantSearchRequestDTO request = ParticipantSearchRequestDTO.builder()
                .page(page)
                .size(size)
                .search(search)
                .tenantId(tenantId)
                .build();
        
        Page<ParticipantResponseDTO> participants = participantService.getParticipants(request);
        return ResponseEntity.ok(participants);
    }
}