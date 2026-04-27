# Spring-Batch

1. `compose.yaml` 로 환경구성을 한다.
2.  배치에 필요한 메타 테이블은 `schema-mysql.sql` 에 위치해있다. 해당 sql 을 로컬 DB 에 적용한다.
   - `schema-mysql.sql` 을 시스템이 읽을 수 있도록 yaml 파일에 platform 설정이 필요하다. 
```   
 batch:
   jdbc:
     platform: mysql
```


## HelloWorldJobConfig
1. Tasklet 작성 
2. Step
   - Step 은 JobRepository 와 사용할 Tasklet 이 필수이다.  
   - Bean 이름과 메서드명이 동일한게 좋다. 
3. Job 
   - Step, Job 은 반드시 1개 이상이어야 한다.

* Yml 에 job.name 을 명시하고 실행하면 된다. 
  * 다만, 일반적인 배치 어플리케이션에 N 개의 Job 이 있으니 Job Name 을 명시적으로 받아서 사용하기 위해 프로퍼티를 지정한다.
    ```
        job:
            name: ${job.name:None}
    ``` 
    * 외부에서 JobName 을 전달해줘야 한다.

## Meta Table 
* `BATCH_JOB_INSTANCE` : 실행 한 Job 의 이력을 볼 수 있다. 
*  `BATCH_JOB_EXECUTION` : `BATCH_JOB_INSTANCE` 와 관계가 있으며, 실행 한 Job 의 시작시간, 종료시간, 상태 등등의 정보를 확인할 수 있다. 
* `BATCH_STEP_EXECUTION` :  `BATCH_JOB_EXECUTION` 와 관계가 있으며, 실행 한 Step 이 몇 개를 읽었는지, 썼는지 등등 디테일 한 정보를 확인할 수 있다. 

# Chuck 
* 관련 properties 설정
* Entity : Payment, PaymentSource 생성 

1. ItemReader : Data 에 맞게 구현체를 선택 (없다면 Custom)
   - 대량의 데이터를 읽는 경우 OutOfMemory 메모리 이슈 발생 가능성이 있다. 

# Fault - Tolerant 

# Spring Batch 성능 최적화 기법 : 각 요소들을 어떻게 최적화 수행하는 방법
* Reader (ItemReader) 
  1. Limit-Offset 방식
  2. No- Offset 방식
  3. Cursor 방식: Stream 처럼 하나하나씩 읽는 방식 
* Processor & Writer : 100번의 I/O 대신에, chuck 단위로 나눠 I/O 를 줄이자 
  * Limit

# Job Execution 의 생성 흐름 
``` 
jobLauncher.run() 호출
│
▼
Spring Batch 내부 (JobRepository)JdbcTemplate 로 
BATCH_JOB_EXECUTION 테이블에 즉시 INSERT → createTime 설정
│
▼
실제 Job 로직 실행 시작 → startTime 설정 (UPDATE)
│
▼
Job 완료
→ endTime, status 업데이트 (UPDATE)`

# Spring Batch 메타 테이블은 JDBC Template 사용 
``` 
Spring Batch 내부 (JobRepository) : 즉시 DB 반영 
│
└── JdbcTemplate (spring-jdbc)
        │
        ├── BATCH_JOB_EXECUTION       ← createTime, startTime, status
        ├── BATCH_JOB_EXECUTION_PARAMS ← Job Parameters
        └── BATCH_STEP_EXECUTION       ← readCount, writeCount 등