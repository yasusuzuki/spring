
create table contract (
  policy_no char(16), 
  contractor_name varchar(16), 
  contractor_address varchar(64), 
  contract_start_date date, 
  product_code varchar(16),
  product_category varchar(16), 
  premium decimal(16,0)
  );
