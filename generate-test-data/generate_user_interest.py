#!/usr/bin/python3
# -*- coding: utf-8 -*-

import random
import pymysql.cursors

WORK_PATH=''

# Connect to the database
connection = pymysql.connect(host='mysql.local',
                             user='dbuser',
                             password='password',
                             db='db',
                             charset='utf8mb4',
                             cursorclass=pymysql.cursors.DictCursor)

file1 = open(WORK_PATH + 'words.shuf', 'r')
try:
    with connection.cursor() as cursor:

        count = 0

        sql = "SELECT id FROM `user_info`"
        cursor.execute(sql)
        ids = cursor.fetchall()


        while True:
            count += 1

            # Get next line from file
            line = file1.readline()

            # if line is empty
            # end of file is reached
            if not line:
                break
            value = line.strip()

            cnt = int(random.uniform(2, 30))
            values = list()

            for i in range(0, cnt):
                row = ids[int(random.uniform(0, len(ids)))]
                values.append(row['id'])

            for id in set(values):
                # Create a new record
                sql = "INSERT INTO `user_interest` (`user_info_id`, `interest`) VALUES (%s, %s)"
                cursor.execute(sql, (id, value))
                # print(sql)
                # print((id, value))
                print(count, sep=' ')


    # connection is not autocommit by default. So you must commit to save
    # your changes.
    connection.commit()

finally:
    connection.close()
    file1.close()
