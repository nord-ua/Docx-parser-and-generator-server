
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.oreilly.servlet.MultipartRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.ModelAndView;
import spark.Request;
import spark.template.mustache.MustacheTemplateEngine;
import utils.ClientData;
import utils.parse.DocumentBuilder;
import utils.parse.DocumentParser;
import utils.parts.ParsedData;
import utils.parts.ParsedPart;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class DocumentServer {

    private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.

    public static void main(String[] args) {

        get("/",(request, response) -> {
            Map map = new HashMap();
            return new ModelAndView(map, "index.mustache");
        }, new MustacheTemplateEngine());

        get("/data",(request, response) -> {
            Map map = new HashMap();
            return new ModelAndView(map, "data.mustache");
        }, new MustacheTemplateEngine());


        post("/upload", (request, response) -> {
            File file = null;
            try {
                file = getFile(request);

                ParsedData parsedData = DocumentParser.parseDocument(file);

                JSONObject result = new JSONObject();
                JSONArray data = new JSONArray();

                for (ParsedPart pp : parsedData.parsedParts) {
                    data.put(pp.getJson());
                }
                result.put("data", data);

                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != file) {
                    file.delete();
                }
            }

            return "Uploaded";
        });


        post("/build", (request, response) -> {
            File file = null;
            try {
                final File upload = new File("upload");
                if (!upload.exists() && !upload.mkdirs()) {
                    throw new RuntimeException("Failed to create directory " + upload.getAbsolutePath());
                }
                final MultipartRequest req = new MultipartRequest(request.raw(), upload.getAbsolutePath());
                file = req.getFile("file");

                ClientData clientData = getClientData(req);

                ParsedData parsedData = DocumentParser.parseDocument(file);

                DocumentBuilder db = new DocumentBuilder(req);
                File result = db.buildDocument(file, parsedData, clientData);
                HttpServletResponse raw = response.raw();
                ServletOutputStream resultFile = response.raw().getOutputStream();

                raw.reset();
                raw.setBufferSize(DEFAULT_BUFFER_SIZE);
                raw.setHeader("Content-Length", String.valueOf(result.length()));
                raw.setContentType("application/x-download");
                raw.setHeader("Content-Disposition", "attachment; filename=Result.docx");

                //utility method
                // Prepare streams.
                BufferedInputStream input = null;
                BufferedOutputStream output = null;

                try {
                    // Open streams.
                    input = new BufferedInputStream(new FileInputStream(result), DEFAULT_BUFFER_SIZE);
                    output = new BufferedOutputStream(raw.getOutputStream(), DEFAULT_BUFFER_SIZE);

                    // Write file contents to response.
                    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                    int length;
                    while ((length = input.read(buffer)) > 0) {
                        output.write(buffer, 0, length);
                    }
                } finally {
                    // Gently close streams.
                    close(output);
                    close(input);
                    result.delete();
                }

                raw.flushBuffer();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != file) {
                    file.delete();
                }
            }

            return "Uploaded";
        });

    }

    private static ClientData getClientData(MultipartRequest req) {
        Gson gson = new Gson();
        JsonElement element = new JsonParser().parse(req.getParameter("data"));
        JsonObject object = element.getAsJsonObject();

        ClientData clientData = gson.fromJson(object, ClientData.class);

        return clientData;
    }

    private static void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static File getFile(Request request) throws IOException {
        final File upload = new File("upload");
        if (!upload.exists() && !upload.mkdirs()) {
            throw new RuntimeException("Failed to create directory " + upload.getAbsolutePath());
        }

        final MultipartRequest req = new MultipartRequest(request.raw(), upload.getAbsolutePath());
        return req.getFile("file");
    }

}