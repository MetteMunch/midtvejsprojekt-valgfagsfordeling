USE valgfagsfordelingdb;

-- Nulstil Priority
UPDATE priority SET fulfilled = 0;

-- Nulstil Course
UPDATE course SET participants_count = 0;

-- Nulstil Student
UPDATE student SET handling_count = 0;





