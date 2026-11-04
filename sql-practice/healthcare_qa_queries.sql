-- 1. Show all patients.
SELECT *
FROM Patients;

-- 2. Show Malaysian patients.
SELECT PatientID, PatientName
FROM Patients
WHERE Country = 'Malaysia';

-- 3. Count patients in each country.
SELECT Country, COUNT(*) AS PatientCount
FROM Patients
GROUP BY Country;

-- 4. Show female patients.
SELECT PatientID, PatientName
FROM Patients
WHERE Gender = 'Female';

-- The Bills table has BillID, PatientID, and Amount.

-- 5. Show bills with an amount greater than 5,000.
SELECT BillID, PatientID, Amount
FROM Bills
WHERE Amount > 5000
ORDER BY Amount DESC;

-- 6. Count the bills for each patient.
SELECT PatientID, COUNT(*) AS TotalBills
FROM Bills
GROUP BY PatientID;

-- 7. Show patients who have more than two bills.
SELECT PatientID
FROM Bills
GROUP BY PatientID
HAVING COUNT(*) > 2;

-- 8. Show patients who do not have any bills.
SELECT p.PatientID, p.PatientName
FROM Patients p
WHERE NOT EXISTS (
    SELECT 1
    FROM Bills b
    WHERE b.PatientID = p.PatientID
);

-- 9. Add up the bill amounts for each patient.
SELECT PatientID, SUM(Amount) AS TotalBillAmount
FROM Bills
GROUP BY PatientID;

-- 10. Show patients whose total bills are greater than 10,000.
SELECT PatientID, SUM(Amount) AS TotalBillAmount
FROM Bills
GROUP BY PatientID
HAVING SUM(Amount) > 10000;

-- The Payments table has PaymentID, BillID, and PaymentAmount.

-- 11. Show bills that do not have a matching payment.
SELECT Bills.BillID, Bills.Amount
FROM Bills
LEFT JOIN Payments
    ON Bills.BillID = Payments.BillID
WHERE Payments.BillID IS NULL;

-- 12. Show each unpaid balance.
SELECT
    Bills.BillID,
    Bills.Amount,
    COALESCE(PaymentTotals.TotalPaid, 0) AS TotalPaid,
    Bills.Amount - COALESCE(PaymentTotals.TotalPaid, 0) AS OutstandingAmount
FROM Bills
LEFT JOIN (
    SELECT BillID, SUM(PaymentAmount) AS TotalPaid
    FROM Payments
    GROUP BY BillID
) AS PaymentTotals
    ON Bills.BillID = PaymentTotals.BillID
WHERE COALESCE(PaymentTotals.TotalPaid, 0) < Bills.Amount;

-- 13. Show bills that have been overpaid.
SELECT
    Bills.BillID,
    Bills.Amount,
    PaymentTotals.TotalPaid,
    PaymentTotals.TotalPaid - Bills.Amount AS OverpaidAmount
FROM Bills
INNER JOIN (
    SELECT BillID, SUM(PaymentAmount) AS TotalPaid
    FROM Payments
    GROUP BY BillID
) AS PaymentTotals
    ON Bills.BillID = PaymentTotals.BillID
WHERE PaymentTotals.TotalPaid > Bills.Amount;

-- 14. Find bills with more than one payment record.
SELECT BillID, COUNT(*) AS PaymentCount
FROM Payments
GROUP BY BillID
HAVING COUNT(*) > 1;

-- 15. Show patients who do not have any bills, including their names.
SELECT p.PatientID, p.PatientName
FROM Patients p
         LEFT JOIN Bills b
                   ON p.PatientID = b.PatientID
WHERE b.PatientID IS NULL;
