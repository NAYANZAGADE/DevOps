-- Create table for PlanEligibility exclusions enum collection
CREATE TABLE IF NOT EXISTS plan_eligibility_exclusion (
    plan_eligibility_id UUID NOT NULL,
    exclusions VARCHAR(64) NOT NULL,
    CONSTRAINT fk_plan_eligibility_exclusion_eligibility
        FOREIGN KEY (plan_eligibility_id)
        REFERENCES plan_eligibility(id)
        ON DELETE CASCADE
);

-- Optional backfill: if old boolean columns exist, copy them into the enum table
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='plan_eligibility' AND column_name='exclude_part_time_employees') THEN
        INSERT INTO plan_eligibility_exclusion (plan_eligibility_id, exclusions)
        SELECT id, 'PART_TIME' FROM plan_eligibility WHERE exclude_part_time_employees IS TRUE;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='plan_eligibility' AND column_name='exclude_union_employees') THEN
        INSERT INTO plan_eligibility_exclusion (plan_eligibility_id, exclusions)
        SELECT id, 'UNION' FROM plan_eligibility WHERE exclude_union_employees IS TRUE;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='plan_eligibility' AND column_name='exclude_non_resident_aliens') THEN
        INSERT INTO plan_eligibility_exclusion (plan_eligibility_id, exclusions)
        SELECT id, 'NON_RESIDENT_ALIEN' FROM plan_eligibility WHERE exclude_non_resident_aliens IS TRUE;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='plan_eligibility' AND column_name='exclude_interns_or_trainees') THEN
        INSERT INTO plan_eligibility_exclusion (plan_eligibility_id, exclusions)
        SELECT id, 'INTERN_OR_TRAINEE' FROM plan_eligibility WHERE exclude_interns_or_trainees IS TRUE;
    END IF;
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='plan_eligibility' AND column_name='exclude_temporary_or_seasonal_workers') THEN
        INSERT INTO plan_eligibility_exclusion (plan_eligibility_id, exclusions)
        SELECT id, 'TEMPORARY_OR_SEASONAL' FROM plan_eligibility WHERE exclude_temporary_or_seasonal_workers IS TRUE;
    END IF;
END $$;
