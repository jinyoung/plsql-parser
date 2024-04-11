CREATE OR REPLACE PROCEDURE calculate_payroll AS 
BEGIN
    FOR rec IN (SELECT e.employee_id, e.base_salary, e.employee_type, e.contract_tax_rate
                FROM employees e) LOOP
        -- 야근 수당 계산
        DECLARE
            overtime_hours NUMBER;
            overtime_rate NUMBER := 1.5;  -- 야근 수당 비율
            overtime_pay NUMBER;
        BEGIN
            SELECT SUM(over_hours)
            INTO overtime_hours
            FROM work_logs
            WHERE employee_id = rec.employee_id
              AND work_date BETWEEN trunc(sysdate, 'MM') AND last_day(sysdate);  -- 현재 월에 해당하는 기록만 선택
            
            IF overtime_hours IS NULL THEN
                overtime_hours := 0;
            END IF;
            
            overtime_pay := overtime_hours * (rec.base_salary / 160) * overtime_rate;  -- 160시간 기준
        END;
        
        -- 무급 휴가 공제 계산
        DECLARE
            unpaid_leave_days NUMBER;
            unpaid_deduction NUMBER;
        BEGIN
            SELECT SUM(leave_days)
            INTO unpaid_leave_days
            FROM leave_records
            WHERE employee_id = rec.employee_id
              AND leave_type = 'Unpaid'
              AND leave_date BETWEEN trunc(sysdate, 'MM') AND last_day(sysdate);  -- 현재 월에 해당하는 기록만 선택
            
            IF unpaid_leave_days IS NULL THEN
                unpaid_leave_days := 0;
            END IF;
            
            unpaid_deduction := (rec.base_salary / 20) * unpaid_leave_days;  -- 월 기준 20일로 계산
        END;
        
        -- 세금 공제 계산
        DECLARE
            tax_rate NUMBER := 0.1;  -- 기본 세금 비율 10%
            contract_tax_rate NUMBER;
            tax_deduction NUMBER;
        BEGIN
            -- 계약직인 경우 세금율을 employees 테이블에서 가져온 값으로 설정
            IF rec.employee_type = 'Contract' THEN
                contract_tax_rate := rec.contract_tax_rate;
            ELSE
                contract_tax_rate := tax_rate;  -- 정규직인 경우 기본 세금율 사용
            END IF;
            
            tax_deduction := (rec.base_salary + overtime_pay - unpaid_deduction) * contract_tax_rate;
        END;
        
        -- 최종 급여 업데이트
        UPDATE employees
        SET final_salary = rec.base_salary + overtime_pay - unpaid_deduction - tax_deduction
        WHERE employee_id = rec.employee_id;
    END LOOP;
    
    COMMIT;
END;
/
