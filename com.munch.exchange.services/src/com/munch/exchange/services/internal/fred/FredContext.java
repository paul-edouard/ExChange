package com.munch.exchange.services.internal.fred;




public enum FredContext {

	INSTANCE;
/*
	static final String FRED_REST_TEMPLATE_ID = "fredRestTemplate";
	static final String FRED_APPLICATION_CONTEXT_XML = "resources/spring/application-context.xml";

	private  ApplicationContext context =null;

	private RestTemplate restTemplate = null;

	private static Logger logger = Logger
			.getLogger(FredContext.class);
	
	
	@Before
	private void setUp() throws Exception {
		File f=new File(FRED_APPLICATION_CONTEXT_XML);
		if(f.exists()){
			context = new FileSystemXmlApplicationContext(FRED_APPLICATION_CONTEXT_XML);
		}
		else{
			
			Bundle bundle = FrameworkUtil.getBundle(this.getClass());
			URL url = FileLocator.find(bundle, new Path(FRED_APPLICATION_CONTEXT_XML), null);
			context = new FileSystemXmlApplicationContext(url.toString());
			
			
		}
		
		restTemplate = (RestTemplate) context.getBean(FRED_REST_TEMPLATE_ID);
	}

	@After
	public void tearDown() throws Exception {
		restTemplate = null;
	}
	
	
	public RestTemplate getRestTemplate(){
		if(restTemplate==null){
			try {
				setUp();
			} catch (Exception e) {
				logger.info("Error by initializing the Fred context");
				e.printStackTrace();
			}
		}
		
		return restTemplate;
	}
*/
}
