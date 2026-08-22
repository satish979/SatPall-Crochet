package com.satpall.crochet.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Reusable, branded HTML email template component for Loomelle Crochet.
 * Single source of truth for all transactional emails so every email shares
 * one consistent design: brand header, content sections, buttons, footer.
 *
 * All markup uses table-based layout with inline styles for maximum
 * compatibility (Gmail mobile/desktop, Outlook, Apple Mail).
 */
public final class LoomelleEmailTemplates {

	public static final String C_WINE = "#8E4C55";
	public static final String C_ROSE = "#C27D86";
	public static final String C_INK = "#2C2320";
	public static final String C_MUTED = "#786C66";
	public static final String C_FAINT = "#A59891";
	public static final String C_BG = "#FAF6F2";
	public static final String C_CARD = "#FFFFFF";
	public static final String C_BORDER = "#EDE4DC";
	public static final String C_SOFT = "#FBF0F1";
	public static final String C_SUCCESS = "#4C7A5C";
	public static final String C_DANGER = "#B04A3E";
	public static final String BRAND = "Loomelle Crochet";
	public static final String TAGLINE = "Artisanal Handcrafted Studio";
	public static final String SUPPORT_EMAIL = "loomellecrochet.support@gmail.com";

	private LoomelleEmailTemplates() {
	}

	/** Escapes user-provided values so email markup can never break. */
	public static String esc(Object value) {
		if (value == null) {
			return "";
		}
		String s = value.toString();
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
				.replace("\"", "&quot;").replace("'", "&#39;");
	}

	public static String money(Object amount) {
		return "\u20B9" + esc(amount);
	}

	/** Formats a LocalDateTime as a friendly date-time string. */
	public static String fmt(LocalDateTime dt) {
		if (dt == null) {
			return "-";
		}
		return dt.format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy, hh:mm a"));
	}

	/**
	 * Wraps arbitrary inner content in the full branded email shell:
	 * outer background, brand header, content, and footer.
	 */
	public static String wrap(String title, String innerHtml) {
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\">")
				.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
				.append("<title>").append(esc(title)).append("</title></head>")
				.append("<body style=\"margin:0;padding:0;background-color:").append(C_BG)
				.append(";font-family:'Segoe UI',Roboto,Helvetica,Arial,sans-serif;color:").append(C_INK).append(";\">")
				// Outer wrapper
				.append("<table role=\"presentation\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"background-color:")
				.append(C_BG).append(";padding:30px 12px;\"><tr><td align=\"center\">")
				// Card
				.append("<table role=\"presentation\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" ")
				.append("style=\"max-width:600px;width:100%;background:").append(C_CARD)
				.append(";border-radius:16px;border:1px solid ").append(C_BORDER)
				.append(";box-shadow:0 4px 20px rgba(142,76,85,0.08);overflow:hidden;\">")
				// Brand header
				.append(header())
				// Content
				.append(innerHtml)
				// Footer
				.append(footer())
				.append("</table></td></tr></table></body></html>");
		return sb.toString();
	}

	/** Branded gradient header with brand name and tagline. */
	private static String header() {
		return "<tr><td style=\"background:linear-gradient(135deg," + C_WINE + " 0%," + C_ROSE + " 100%);"
				+ "padding:28px 24px;text-align:center;\">"
				+ "<h1 style=\"margin:0;color:#FFFFFF;font-size:24px;letter-spacing:1px;font-weight:700;\">" + BRAND + "</h1>"
				+ "<p style=\"margin:6px 0 0;color:rgba(255,255,255,0.85);font-size:13px;letter-spacing:0.5px;\">" + TAGLINE + "</p>"
				+ "</td></tr>";
	}

	/** Professional branded footer with support contact and copyright. */
	private static String footer() {
		return "<tr><td style=\"background:" + C_BG + ";border-top:1px solid " + C_BORDER + ";padding:18px 24px;text-align:center;\">"
				+ "<p style=\"margin:0;font-size:12px;color:" + C_FAINT + ";line-height:1.6;\">"
				+ "Need help? Email us at <a href=\"mailto:" + SUPPORT_EMAIL + "\" style=\"color:" + C_WINE + ";text-decoration:none;\">" + SUPPORT_EMAIL + "</a></p>"
				+ "<p style=\"margin:6px 0 0;font-size:11px;color:" + C_ROSE + ";\">"
				+ "&copy; " + BRAND + " &bull; Handcrafted with Love &bull; All rights reserved.</p>"
				+ "</td></tr>";
	}

	/** A titled content section (card) inside the email body. */
	public static String section(String title, String innerHtml) {
		StringBuilder sb = new StringBuilder("<tr><td style=\"padding:24px 28px 8px 28px;\">");
		if (title != null && !title.isEmpty()) {
			sb.append("<h3 style=\"margin:0 0 12px;color:").append(C_INK).append(";font-size:16px;font-weight:600;")
					.append("border-bottom:2px solid ").append(C_SOFT).append(";padding-bottom:8px;\">")
					.append(title).append("</h3>");
		}
		sb.append(innerHtml).append("</td></tr>");
		return sb.toString();
	}

	/** Full-width padded content block without a title. */
	public static String block(String innerHtml) {
		return "<tr><td style=\"padding:20px 28px;\">" + innerHtml + "</td></tr>";
	}

	/** Key/value detail row used in summary tables. */
	public static String kvRow(String label, Object value) {
		return "<tr>"
				+ "<td style=\"padding:7px 0;color:" + C_MUTED + ";font-size:13px;width:42%;vertical-align:top;\">" + esc(label) + "</td>"
				+ "<td style=\"padding:7px 0;color:" + C_INK + ";font-size:13px;font-weight:600;vertical-align:top;\">" + esc(value) + "</td>"
				+ "</tr>";
	}

	/** Raw key/value row where the value is pre-built HTML (e.g. badges). */
	public static String kvRowHtml(String label, String valueHtml) {
		return "<tr>"
				+ "<td style=\"padding:7px 0;color:" + C_MUTED + ";font-size:13px;width:42%;vertical-align:top;\">" + esc(label) + "</td>"
				+ "<td style=\"padding:7px 0;color:" + C_INK + ";font-size:13px;font-weight:600;vertical-align:top;\">" + valueHtml + "</td>"
				+ "</tr>";
	}

	/** Wraps rows in a soft rounded detail card. */
	public static String kvTable(String rowsHtml) {
		return "<table role=\"presentation\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" "
				+ "style=\"background:" + C_BG + ";border-radius:10px;border-collapse:collapse;\">"
				+ rowsHtml + "</table>";
	}

	/** Colored pill badge for statuses. */
	public static String badge(String text, String color, String bg) {
		return "<span style=\"display:inline-block;background:" + bg + ";color:" + color
				+ ";padding:4px 12px;border-radius:20px;font-size:12px;font-weight:700;letter-spacing:0.5px;\">"
				+ esc(text) + "</span>";
	}

	/** Call-to-action button. */
	public static String button(String url, String label) {
		if (url == null || url.isEmpty()) {
			return "";
		}
		return "<table role=\"presentation\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"margin:18px auto 4px auto;\"><tr><td align=\"center\" "
				+ "style=\"background:" + C_WINE + ";border-radius:8px;mso-padding-alt:12px 28px;\">"
				+ "<a href=\"" + esc(url) + "\" style=\"display:inline-block;padding:12px 28px;font-size:14px;font-weight:600;"
				+ "color:#FFFFFF;text-decoration:none;border-radius:8px;\">" + esc(label) + "</a>"
				+ "</td></tr></table>";
	}

	/** Highlighted note box (info/success/warning). */
	public static String noteBox(String text, String borderColor) {
		return "<div style=\"background:" + C_BG + ";border-left:4px solid " + borderColor
				+ ";padding:14px 16px;border-radius:6px;margin:0;\">"
				+ "<p style=\"margin:0;color:#4A3E39;font-size:13px;line-height:1.6;white-space:pre-wrap;\">" + text + "</p></div>";
	}

	/** Greeting line at the top of customer emails. */
	public static String greeting(String customerName) {
		String name = (customerName == null || customerName.trim().isEmpty()) ? "Valued Customer" : customerName.trim();
		return "<p style=\"margin:0 0 14px;color:" + C_INK + ";font-size:15px;\">Dear <strong>" + esc(name) + "</strong>,</p>";
	}

	/** Standard paragraph styling. */
	public static String p(String text) {
		return "<p style=\"margin:0 0 12px;color:" + C_MUTED + ";font-size:14px;line-height:1.65;\">" + text + "</p>";
	}
}
