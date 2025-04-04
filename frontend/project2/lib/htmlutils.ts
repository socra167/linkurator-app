/**
 * Strips HTML tags from a string and returns plain text
 * @param html HTML string to strip tags from
 * @param maxLength Optional maximum length for the returned text
 * @returns Plain text with HTML tags removed
 */
export function stripHtml(html: string, maxLength?: number): string {
  if (!html) return "";

  // Create a temporary DOM element
  const doc = new DOMParser().parseFromString(html, "text/html");
  // Get the text content
  let text = doc.body.textContent || "";

  // Trim and limit length if specified
  text = text.trim();
  if (maxLength && text.length > maxLength) {
    text = text.substring(0, maxLength) + "...";
  }

  return text;
}

/**
 * Safely truncates HTML content to a specified length
 * For use with dangerouslySetInnerHTML when you want to preserve formatting
 * @param html HTML string to truncate
 * @param maxLength Maximum length for the returned HTML
 * @returns Truncated HTML string
 */
export function truncateHtml(html: string, maxLength: number): string {
  if (!html || html.length <= maxLength) return html;

  // Create a temporary DOM element
  const doc = new DOMParser().parseFromString(html, "text/html");
  const allNodes = Array.from(doc.body.querySelectorAll("*"));

  let totalLength = 0;
  let truncated = false;

  for (const node of allNodes) {
    if (node.textContent) {
      const nodeTextLength = node.textContent.length;

      if (totalLength + nodeTextLength > maxLength) {
        // This node would exceed the limit
        const remainingLength = maxLength - totalLength;
        if (
          remainingLength > 0 &&
          node.firstChild &&
          node.firstChild.nodeType === Node.TEXT_NODE
        ) {
          // Truncate this text node
          node.firstChild.textContent =
            node.firstChild.textContent!.substring(0, remainingLength) + "...";
        }
        truncated = true;
        break;
      }

      totalLength += nodeTextLength;
    }

    if (truncated) {
      // Remove all subsequent nodes
      const siblings = [];
      let nextSibling = node.nextSibling;
      while (nextSibling) {
        siblings.push(nextSibling);
        nextSibling = nextSibling.nextSibling;
      }
      siblings.forEach((sibling) => sibling.parentNode?.removeChild(sibling));
    }
  }

  return doc.body.innerHTML;
}
