alter table three_ds_server_storage.serial_num
    rename to serial_number;

alter table three_ds_server_storage.serial_number
    rename column serial_num TO serial_number;

alter table three_ds_server_storage.serial_number
    rename constraint serial_num_pkey to serial_number_pkey;
