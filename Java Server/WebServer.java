// Della Matera Lorenzo 5E

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.concurrent.*;

import com.sun.net.httpserver.*;

public class WebServer {
	
	public WebServer() {
		
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress("10.1.1.4", 8000), 0);
			server.createContext("/", new  MyHttpHandler());
			server.createContext("/favicon.ico", t -> {
				byte[] bytes = Files.readAllBytes(Paths.get("data/icon.png"));
				t.sendResponseHeaders(200, bytes.length);
				try (OutputStream os = t.getResponseBody()) {
					os.write(bytes);
				}
			});
			ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(10);
			server.setExecutor(threadPoolExecutor);
			server.start();
			System.out.println("________________Web server started -> 10.1.1.4:8000________________");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	class MyHttpHandler implements HttpHandler{
		@Override    
		public void handle(HttpExchange httpExchange) throws IOException {
			String requestParams[] = null;
			//System.out.println(httpExchange.getRemoteAddress().getAddress().toString().equals("/80.211.73.225"));
			if("GET".equals(httpExchange.getRequestMethod())) { 
				requestParams = handleGetRequest(httpExchange).split("/");
			}
			try {
				handleResponse(httpExchange, "/"+requestParams[2], requestParams[1]);
			} catch(ArrayIndexOutOfBoundsException e){
				handleResponse(httpExchange, "/", requestParams[1]);
			}
		}
		
		private String handleGetRequest(HttpExchange httpExchange) {
			String url = httpExchange.getRequestURI().toString();
			//System.out.println(url);
			if(!url.startsWith("/")) "/".concat(url);
			return url;
		}
		
		private void handleResponse(HttpExchange httpExchange, String requestParamValue, String key) throws IOException {
			OutputStream outputStream = httpExchange.getResponseBody();
			
			String htmlResponse = "";
			int code = 200;

			LampClient client = Clients.get(key);
			
			//if(httpExchange.getRemoteAddress().getAddress().toString().equals("/80.211.73.225")){
				if(client!=null) {
					if( requestParamValue.contains("mode") ||
						requestParamValue.contains("set_color_hash") ||
						requestParamValue.contains("set_brightness_hash") ||
						requestParamValue.contains("voice") ||
						requestParamValue.contains("random")) {

							client.stopAutomator();
							client.send(requestParamValue);
							byte[] file = "success".getBytes();
							if(file!=null) {
								httpExchange.sendResponseHeaders(code, file.length);
								outputStream.write(file);
								outputStream.flush();
								outputStream.close();
								return;
							}
					}
					else if(requestParamValue.contains("automator")){
						String params = httpExchange.getRequestURI().getQuery();
						if(params.contains("start")){
							client.stopAutomator();
							String idAutomation = params.split("=")[2];
							client.startAutomator(Integer.parseInt(idAutomation));
						}
						else if(params.contains("stop")){
							client.stopAutomator();
						}
						else if(params.contains("update")){
							String idAutomation = params.split("=")[2];
							client.updateAutomator(Integer.parseInt(idAutomation));
						}
						byte[] file = "success".getBytes();
						if(file!=null) {
							httpExchange.sendResponseHeaders(code, file.length);
							outputStream.write(file);
							outputStream.flush();
							outputStream.close();
							return;
						}
					}
					else if(requestParamValue.contains("restart")) {
						client.restart();
					}
					else {
						byte[] file = getFilePage(requestParamValue);
						if(file!=null) {
							httpExchange.sendResponseHeaders(code, file.length);
							outputStream.write(file);
							outputStream.flush();
							outputStream.close();
							return;
						}
						else {
							code = 404;
							htmlResponse = "404: not found";
						}
						
					}
					
				}
				else {
					code = 500;
					htmlResponse = "client not connected!";
				}
			/*}
			else {
				code = 403;
				htmlResponse = "403: forbidden";
			}*/
			String page = "<html>"
					+ "<head><title>LedLamps</title></head>"
					+ "<body>" + htmlResponse + "</body>"
					+ "</html>";
			httpExchange.sendResponseHeaders(code, page.length());
			outputStream.write(page.getBytes());
			outputStream.flush();
			outputStream.close();
		}
		
		private byte[] getFilePage(String path) throws IOException{
			byte[] file = null;
			if(path.endsWith("/"))
				path = "/index.html";
			File f = new File("data"+path);
			if(f!=null)
				file = Files.readAllBytes(f.toPath());
			return file;
		}
	}
}
